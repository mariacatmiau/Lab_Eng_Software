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

    public Doacao criar(CriarDoacaoRequest req) {
        if (req.produtoId == null || req.ongId == null || req.criadoPorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campos obrigatórios ausentes");
        }

        Produto produto = produtoRepo.findById(req.produtoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        Usuario ong = usuarioRepo.findById(req.ongId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada"));

        Usuario criador = usuarioRepo.findById(req.criadoPorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário criador não encontrado"));

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
        return doacaoRepo.findAll();
    }

    public List<Doacao> listarPorOng(Long id) {
        Usuario ong = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada"));
        return doacaoRepo.findByOng(ong);
    }

    public Doacao aceitar(Long id) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));
        d.setStatus(StatusDoacao.ACEITA);
        return doacaoRepo.save(d);
    }

    public Doacao recusar(Long id) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));
        d.setStatus(StatusDoacao.RECUSADA);
        return doacaoRepo.save(d);
    }

    public Doacao confirmarRetirada(Long id) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));
        d.setStatus(StatusDoacao.RETIRADA);
        return doacaoRepo.save(d);
    }
}
