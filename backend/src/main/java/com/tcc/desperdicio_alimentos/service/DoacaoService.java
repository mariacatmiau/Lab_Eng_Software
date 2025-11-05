package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarDoacaoRequest;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepo;
    private final ProdutoRepository produtoRepo;
    private final OngRepository ongRepo;
    private final FuncionarioRepository funcionarioRepo;
    private final ProdutoService produtoService;

    public DoacaoService(DoacaoRepository d, ProdutoRepository p, OngRepository o,
                         FuncionarioRepository f, ProdutoService produtoService) {
        this.doacaoRepo = d;
        this.produtoRepo = p;
        this.ongRepo = o;
        this.funcionarioRepo = f;
        this.produtoService = produtoService;
    }

    public Doacao criar(CriarDoacaoRequest req) {
        Produto produto = produtoRepo.findById(req.produtoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        if (!produto.isDisponivel()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto indisponível");

        Ong ong = ongRepo.findById(req.ongId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada"));

        Funcionario func = funcionarioRepo.findById(req.funcionarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        Doacao d = new Doacao();
        d.setProduto(produto);
        d.setOng(ong);
        d.setCriadoPor(func);
        d.setStatus(StatusDoacao.PENDENTE);
        Doacao salvo = doacaoRepo.save(d);

        // opcional: torna o produto indisponível enquanto está em doação pendente
        produto.setDisponivel(false);
        produtoRepo.save(produto);

        return salvo;
    }

    public List<Doacao> listarPendentes() {
        return doacaoRepo.findByStatus(StatusDoacao.PENDENTE);
    }

    public List<Doacao> listarPendentesDaOng(Long ongId) {
        Ong ong = ongRepo.findById(ongId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ONG não encontrada"));
        return doacaoRepo.findByOngAndStatus(ong, StatusDoacao.PENDENTE);
    }

    public Doacao aceitar(Long id) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));
        if (d.getStatus() != StatusDoacao.PENDENTE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Somente doações pendentes podem ser aceitas");

        d.setStatus(StatusDoacao.ACEITA);
        d.setDataAceite(LocalDateTime.now());
        return doacaoRepo.save(d);
    }

    public Doacao recusar(Long id, String motivo) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));
        if (d.getStatus() != StatusDoacao.PENDENTE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Somente doações pendentes podem ser recusadas");

        d.setStatus(StatusDoacao.RECUSADA);
        d.setMotivoRecusa(motivo);
        // ao recusar, liberar o produto novamente
        Produto p = d.getProduto();
        p.setDisponivel(true);
        produtoRepo.save(p);
        return doacaoRepo.save(d);
    }

    public Doacao marcarRetirada(Long id) {
        Doacao d = doacaoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doação não encontrada"));
        if (d.getStatus() != StatusDoacao.ACEITA)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Somente doações aceitas podem ser retiradas");

        d.setStatus(StatusDoacao.RETIRADA);
        d.setDataRetirada(LocalDateTime.now());
        return doacaoRepo.save(d);
    }
}
