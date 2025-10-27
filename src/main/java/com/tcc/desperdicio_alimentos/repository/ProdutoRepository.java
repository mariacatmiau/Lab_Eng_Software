package com.tcc.desperdicio_alimentos.repository;



import com.tcc.desperdicio_alimentos.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

