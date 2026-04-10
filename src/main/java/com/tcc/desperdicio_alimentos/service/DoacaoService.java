package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarDoacaoRequest;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepo;
    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;

    public DoacaoService(DoacaoRepository doacaoRepo, ProdutoRepository produtoRepo, UsuarioRepository usuarioRepo) {
        this.doacaoRepo = doacaoRepo;
        this.produtoRepo = produtoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public Doacao criar(CriarDoacaoRequest req, Long criadoPorId) {
        if (req.produtoId == null || req.ongId == null || criadoPorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campos obrigatórios ausentes");
        }

        Produto produto = produtoRepo.findById(req.produtoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        Usuario ong = usuarioRepo.findById(req.ongId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada"));

        Usuario criador = usuarioRepo.findById(criadoPorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário criador não encontrado"));

        if (produto.getCriadoPor() == null || !produto.getCriadoPor().getId().equals(criador.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Produto não pertence ao usuário informado");
        }

        Doacao d = new Doacao();
        d.setProduto(produto);
        d.setOng(ong);
        d.setQuantidade(req.quantidade != null ? req.quantidade : produto.getQuantidade());
        d.setStatus(StatusDoacao.PENDENTE);
        d.setCriadoPor(criador);

        produto.setDisponivel(false);
        produtoRepo.save(produto);

        return doacaoRepo.save(d);
    }

    public List<Doacao> listarTodas() {
        reconciliarDoacoesDeProdutosLegados();
        return doacaoRepo.findAll();
    }

    public List<Doacao> listarPorCriador(Long criadorId) {
        reconciliarDoacoesDeProdutosLegados();
        return doacaoRepo.findByCriadoPorId(criadorId);
    }

    public List<Doacao> listarPorOng(Long id) {
        reconciliarDoacoesDeProdutosLegados();
        return doacaoRepo.findByOngId(id);
    }

    public Doacao aceitar(Long id, Long usuarioId) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));

        if (d.getOng() == null || !d.getOng().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas a ONG destinatária pode aceitar a doação");
        }

        if (d.getStatus() != StatusDoacao.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Apenas doações pendentes podem ser aceitas");
        }

        d.setStatus(StatusDoacao.ACEITA);
        return doacaoRepo.save(d);
    }

    public Doacao recusar(Long id, Long usuarioId) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));

        if (!usuarioParticipaDaDoacao(d, usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas participantes da doação podem recusá-la");
        }

        if (d.getStatus() != StatusDoacao.PENDENTE && d.getStatus() != StatusDoacao.ACEITA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Doação já finalizada, não pode ser recusada");
        }

        d.setStatus(StatusDoacao.RECUSADA);
        return doacaoRepo.save(d);
    }

    public Doacao confirmarRetirada(Long id, Long usuarioId) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));

        if (!usuarioParticipaDaDoacao(d, usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas participantes da doação podem confirmar retirada");
        }

        if (d.getStatus() != StatusDoacao.ACEITA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Apenas doações aceitas podem ter retirada confirmada");
        }

        d.setStatus(StatusDoacao.RETIRADA);
        return doacaoRepo.save(d);
    }

    private boolean usuarioParticipaDaDoacao(Doacao doacao, Long usuarioId) {
        return (doacao.getCriadoPor() != null && doacao.getCriadoPor().getId().equals(usuarioId))
                || (doacao.getOng() != null && doacao.getOng().getId().equals(usuarioId));
    }

    private void reconciliarDoacoesDeProdutosLegados() {
        List<Produto> produtosDoacao = produtoRepo.findAll().stream()
                .filter(p -> p.getTipoOferta() == TipoOfertaProduto.DOACAO)
                .filter(p -> p.getOngDestino() != null)
                .toList();

        for (Produto p : produtosDoacao) {
            if (doacaoRepo.existsByProdutoId(p.getId())) {
                continue;
            }

            Doacao d = new Doacao();
            d.setProduto(p);
            d.setOng(p.getOngDestino());
            d.setCriadoPor(p.getCriadoPor());
            d.setQuantidade(p.getQuantidade());
            d.setStatus(StatusDoacao.PENDENTE);
            doacaoRepo.save(d);

            if (Boolean.TRUE.equals(p.getDisponivel())) {
                p.setDisponivel(false);
                produtoRepo.save(p);
            }
        }
    }
}
