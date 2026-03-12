package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.dto.OfertaProdutoDTO;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.model.UsuarioTipo;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoServiceTest {

    private ProdutoService service;
    private ProdutoRepository produtoRepo;
    private UsuarioRepository usuarioRepo;
    private DoacaoRepository doacaoRepo;
    private LocalizacaoService localizacaoService;

    @BeforeEach
    void setup() {
        produtoRepo = mock(ProdutoRepository.class);
        usuarioRepo = mock(UsuarioRepository.class);
        doacaoRepo = mock(DoacaoRepository.class);
        localizacaoService = mock(LocalizacaoService.class);
        service = new ProdutoService(produtoRepo, usuarioRepo, doacaoRepo, localizacaoService);
    }

    @Test
    void deveCriarProduto() {
        CriarProdutoRequest req = new CriarProdutoRequest();
        req.nome = "Arroz";
        req.categoria = "Alimento";
        req.quantidade = 5;
        req.dataValidade = LocalDate.now();
        req.usuarioId = 1L;
        req.tipoOferta = "VENDA";
        req.preco = new BigDecimal("10.50");

        Usuario u = new Usuario();
        u.setId(1L);

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(u));
        when(produtoRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Produto p = service.criar(req);

        assertNotNull(p);
        assertEquals("Arroz", p.getNome());
        assertTrue(p.getDisponivel());
        assertEquals(new BigDecimal("10.50"), p.getPreco());
        verify(produtoRepo).save(any());
    }

    @Test
    void deveFalharQuandoUsuarioNaoExiste() {
        CriarProdutoRequest req = new CriarProdutoRequest();
        req.usuarioId = 99L;

        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.criar(req));
    }

    @Test
    void deveOrdenarOfertasPorDistanciaQuandoLocalizacaoDisponivel() {
        Usuario mercadoLonge = new Usuario();
        mercadoLonge.setId(10L);
        mercadoLonge.setNome("Mercado Longe");
        mercadoLonge.setEndereco("Rua Longe, 100");
        mercadoLonge.setTipo(UsuarioTipo.FUNCIONARIO);

        Usuario mercadoPerto = new Usuario();
        mercadoPerto.setId(11L);
        mercadoPerto.setNome("Mercado Perto");
        mercadoPerto.setEndereco("Rua Perto, 50");
        mercadoPerto.setTipo(UsuarioTipo.FUNCIONARIO);

        Produto produtoLonge = new Produto();
        produtoLonge.setId(1L);
        produtoLonge.setNome("Arroz");
        produtoLonge.setCategoria("Alimento");
        produtoLonge.setPreco(new BigDecimal("12.00"));
        produtoLonge.setQuantidade(3);
        produtoLonge.setDisponivel(true);
        produtoLonge.setTipoOferta(com.tcc.desperdicio_alimentos.model.TipoOfertaProduto.VENDA);
        produtoLonge.setCriadoPor(mercadoLonge);

        Produto produtoPerto = new Produto();
        produtoPerto.setId(2L);
        produtoPerto.setNome("Feijão");
        produtoPerto.setCategoria("Alimento");
        produtoPerto.setPreco(new BigDecimal("9.00"));
        produtoPerto.setQuantidade(2);
        produtoPerto.setDisponivel(true);
        produtoPerto.setTipoOferta(com.tcc.desperdicio_alimentos.model.TipoOfertaProduto.VENDA);
        produtoPerto.setCriadoPor(mercadoPerto);

        when(produtoRepo.findByDisponivelTrue()).thenReturn(List.of(produtoLonge, produtoPerto));
        when(localizacaoService.buscarCoordenadas("Rua Longe, 100"))
                .thenReturn(Optional.of(new LocalizacaoService.Coordenada(-23.7000, -46.7000)));
        when(localizacaoService.buscarCoordenadas("Rua Perto, 50"))
                .thenReturn(Optional.of(new LocalizacaoService.Coordenada(-23.5600, -46.6400)));
        when(localizacaoService.calcularDistanciaKm(-23.5505, -46.6333, -23.7000, -46.7000)).thenReturn(18.4);
        when(localizacaoService.calcularDistanciaKm(-23.5505, -46.6333, -23.5600, -46.6400)).thenReturn(1.3);

        List<OfertaProdutoDTO> ofertas = service.listarOfertas(null, "PRODUTO", null, -23.5505, -46.6333);

        assertEquals(2, ofertas.size());
        assertEquals("Mercado Perto", ofertas.get(0).mercadoNome);
        assertEquals(1.3, ofertas.get(0).distanciaKm);
        assertEquals("Mercado Longe", ofertas.get(1).mercadoNome);
        assertEquals(18.4, ofertas.get(1).distanciaKm);
    }
}
