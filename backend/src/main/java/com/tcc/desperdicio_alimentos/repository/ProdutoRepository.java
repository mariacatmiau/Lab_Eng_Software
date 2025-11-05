package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.model.Funcionario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByDisponivelTrue();
    List<Produto> findByDataValidadeBetweenAndDisponivelTrue(LocalDate ini, LocalDate fim);
    List<Produto> findByCriadoPor(Funcionario f);
}

