package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.dto.OfertaProdutoDTO;
import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.model.StatusDoacao;
import com.tcc.desperdicio_alimentos.model.TipoOfertaProduto;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.model.UsuarioTipo;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;
    private final DoacaoRepository doacaoRepo;
    private final LocalizacaoService localizacaoService;

    public ProdutoService(ProdutoRepository produtoRepo, UsuarioRepository usuarioRepo, DoacaoRepository doacaoRepo,
                          LocalizacaoService localizacaoService) {
        this.produtoRepo = produtoRepo;
        this.usuarioRepo = usuarioRepo;
        this.doacaoRepo = doacaoRepo;
        this.localizacaoService = localizacaoService;
    }

    // Criar produto
    public Produto criar(CriarProdutoRequest req) {
        Usuario u = usuarioRepo.findById(req.usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        TipoOfertaProduto tipoOferta = parseTipoOferta(req.tipoOferta);
        Usuario ongDestino = null;

        Produto p = new Produto();
        p.setNome(req.nome);
        p.setCategoria(req.categoria);
        p.setDataValidade(req.dataValidade);
        p.setQuantidade(req.quantidade);
        p.setDisponivel(true);
        p.setCriadoPor(u);
        p.setTipoOferta(tipoOferta);

        if (tipoOferta == TipoOfertaProduto.VENDA) {
            if (req.preco == null || req.preco.signum() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço é obrigatório para produto de venda");
            }
            p.setPreco(req.preco);
            p.setOngDestino(null);
        } else {
            if (req.ongId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione uma ONG para produtos de doação");
            }

            ongDestino = usuarioRepo.findById(req.ongId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada"));

            if (ongDestino.getTipo() != UsuarioTipo.ONG) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário selecionado não é uma ONG");
            }

            p.setPreco(null);
            p.setOngDestino(ongDestino);
            // A doacao eh aberta imediatamente no cadastro do produto.
            p.setDisponivel(false);
        }

        Produto salvo = produtoRepo.save(p);

        if (tipoOferta == TipoOfertaProduto.DOACAO && ongDestino != null) {
            Doacao doacao = new Doacao();
            doacao.setProduto(salvo);
            doacao.setOng(ongDestino);
            doacao.setCriadoPor(u);
            doacao.setQuantidade(req.quantidade != null ? req.quantidade : salvo.getQuantidade());
            doacao.setStatus(StatusDoacao.PENDENTE);
            doacaoRepo.save(doacao);
        }

        return salvo;
    }

    // Listar apenas produtos disponíveis
    public List<Produto> listarDisponiveis() {
        return produtoRepo.findByDisponivelTrue()
            .stream()
            .filter(p -> p.getTipoOferta() == null || p.getTipoOferta() == TipoOfertaProduto.DOACAO)
            .collect(Collectors.toList());
    }

    // Listar produtos criados por um usuário específico
    public List<Produto> listarPorUsuario(Long usuarioId) {
        Usuario u = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return produtoRepo.findByCriadoPor(u);
    }

    // Listar todos os produtos (para debug/admin)
    public List<Produto> listarTodos() {
        return produtoRepo.findAll();
    }

    // Marcar produto como indisponível
    public Produto marcarIndisponivel(Long id) {
        Produto p = produtoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        p.setDisponivel(false);
        return produtoRepo.save(p);
    }

    public List<OfertaProdutoDTO> listarOfertas(String busca, String tipoBusca, String referencia) {
        return listarOfertas(busca, tipoBusca, referencia, null, null);
        }

        public List<OfertaProdutoDTO> listarOfertas(String busca, String tipoBusca, String referencia,
                            Double latitude, Double longitude) {
        String termo = normalizar(busca);
        String referenciaNorm = normalizar(referencia);
        String tipoBuscaNorm = normalizar(tipoBusca);
        boolean buscarMercado = "MERCADO".equals(tipoBuscaNorm);
        boolean temCoordenadasUsuario = latitude != null && longitude != null;
        Map<String, Optional<LocalizacaoService.Coordenada>> cacheMercados = new HashMap<>();

        return produtoRepo.findByDisponivelTrue()
                .stream()
            .filter(p -> p.getTipoOferta() == TipoOfertaProduto.VENDA)
                .filter(p -> p.getCriadoPor() != null && p.getCriadoPor().getTipo() == UsuarioTipo.FUNCIONARIO)
                .filter(p -> filtroBusca(p, termo, buscarMercado))
                .map(OfertaProdutoDTO::from)
            .peek(dto -> preencherDistancia(dto, latitude, longitude, temCoordenadasUsuario, cacheMercados))
            .sorted(comparadorOfertas(referenciaNorm, temCoordenadasUsuario))
                .collect(Collectors.toList());
    }

        private Comparator<OfertaProdutoDTO> comparadorOfertas(String referenciaNorm, boolean temCoordenadasUsuario) {
        Comparator<OfertaProdutoDTO> comparador = Comparator
            .comparingInt((OfertaProdutoDTO dto) -> scoreProximidade(dto.mercadoEndereco, dto.mercadoNome, referenciaNorm))
            .thenComparing(dto -> dto.dataValidade, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(dto -> normalizar(dto.produtoNome))
            .thenComparing(dto -> normalizar(dto.mercadoNome));

        if (!temCoordenadasUsuario) {
            return comparador;
        }

        return Comparator
            .comparingDouble((OfertaProdutoDTO dto) -> dto.distanciaKm != null ? dto.distanciaKm : Double.MAX_VALUE)
            .thenComparing(comparador);
        }

    private boolean filtroBusca(Produto produto, String termo, boolean buscarMercado) {
        if (termo == null || termo.isBlank()) {
            return true;
        }

        if (buscarMercado) {
            return normalizar(produto.getCriadoPor().getNome()).contains(termo)
                    || normalizar(produto.getCriadoPor().getEndereco()).contains(termo);
        }

        return normalizar(produto.getNome()).contains(termo)
                || normalizar(produto.getCategoria()).contains(termo);
    }

    private void preencherDistancia(OfertaProdutoDTO dto, Double latitude, Double longitude, boolean temCoordenadasUsuario,
                                    Map<String, Optional<LocalizacaoService.Coordenada>> cacheMercados) {
        if (!temCoordenadasUsuario) {
            dto.distanciaKm = null;
            return;
        }

        if (dto.mercadoLatitude != null && dto.mercadoLongitude != null) {
            dto.distanciaKm = localizacaoService.calcularDistanciaKm(
                    latitude,
                    longitude,
                    dto.mercadoLatitude,
                    dto.mercadoLongitude
            );
            return;
        }

        String endereco = dto.mercadoEndereco == null ? "" : dto.mercadoEndereco.trim();
        if (endereco.isBlank()) {
            dto.distanciaKm = null;
            return;
        }

        Optional<LocalizacaoService.Coordenada> coordenadaMercado = cacheMercados.computeIfAbsent(
                endereco.toUpperCase(Locale.ROOT),
                ignored -> localizacaoService.buscarCoordenadas(endereco)
        );

        dto.distanciaKm = coordenadaMercado
                .map(coord -> localizacaoService.calcularDistanciaKm(latitude, longitude, coord.latitude(), coord.longitude()))
                .orElse(null);
    }

    private int scoreProximidade(Produto produto, String referenciaNorm) {
        return scoreProximidade(
                produto.getCriadoPor() != null ? produto.getCriadoPor().getEndereco() : "",
                produto.getCriadoPor() != null ? produto.getCriadoPor().getNome() : "",
                referenciaNorm
        );
    }

    private int scoreProximidade(String enderecoMercado, String nomeMercado, String referenciaNorm) {
        if (referenciaNorm == null || referenciaNorm.isBlank()) {
            return 1;
        }

        String endereco = normalizar(enderecoMercado);
        String nome = normalizar(nomeMercado);

        if (endereco.contains(referenciaNorm)) {
            return 0;
        }
        if (nome.contains(referenciaNorm)) {
            return 1;
        }
        return 2;
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);
    }

    private TipoOfertaProduto parseTipoOferta(String tipoOferta) {
        if (tipoOferta == null || tipoOferta.isBlank()) {
            return TipoOfertaProduto.DOACAO;
        }

        try {
            return TipoOfertaProduto.valueOf(tipoOferta.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de oferta inválido. Use VENDA ou DOACAO");
        }
    }
}
