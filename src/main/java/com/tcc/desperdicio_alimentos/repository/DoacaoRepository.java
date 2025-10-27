package com.tcc.desperdicio_alimentos.repository;



import com.tcc.desperdicio_alimentos.model.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
}
