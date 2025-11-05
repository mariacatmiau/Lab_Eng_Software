package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.model.Funcionario;
import com.tcc.desperdicio_alimentos.repository.FuncionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepo;
    private final FuncionarioRepository funcionarioRepo;

    public ProdutoService(ProdutoRepository p, FuncionarioRepository f) {
        this.produtoRepo = p; this.funcionarioRepo = f;
    }

    public Produto criar(CriarProdutoRequest req) {
        Funcionario f = funcionarioRepo.findById(req.funcionarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionário não encontrado"));

        Produto p = new Produto();
        p.setNome(req.nome);
        p.setDescricao(req.descricao);
        p.setDataValidade(req.dataValidade);
        p.setQuantidade(req.quantidade);
        p.setDisponivel(true);
        p.setCriadoPor(f);
        return produtoRepo.save(p);
    }

    public List<Produto> listarDisponiveis() {
        return produtoRepo.findByDisponivelTrue();
    }

    public List<Produto> proximosVencimento(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);
        return produtoRepo.findByDataValidadeBetweenAndDisponivelTrue(hoje, limite);
    }

    public Produto marcarIndisponivel(Long id) {
        Produto p = produtoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        p.setDisponivel(false);
        return produtoRepo.save(p);
    }
}
