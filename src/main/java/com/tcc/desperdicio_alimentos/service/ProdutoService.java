package com.tcc.desperdicio_alimentos.service;



import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }
}
