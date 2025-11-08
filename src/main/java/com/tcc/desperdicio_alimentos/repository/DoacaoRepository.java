package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.model.StatusDoacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
    List<Doacao> findByStatus(StatusDoacao status);
}
