package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepo;

    public ProdutoService(ProdutoRepository produtoRepo) {
        this.produtoRepo = produtoRepo;
    }

    public Produto criar(CriarProdutoRequest req) {
        Produto p = new Produto();
        p.setNome(req.nome);
        p.setCategoria(req.categoria);
        p.setDataValidade(req.dataValidade);
        p.setQuantidade(req.quantidade);
        p.setDisponivel(true);
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
