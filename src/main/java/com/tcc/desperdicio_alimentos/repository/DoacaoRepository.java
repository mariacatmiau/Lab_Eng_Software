package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
    List<Doacao> findByOng(Usuario ong);
    List<Doacao> findByStatus(StatusDoacao status);
}
