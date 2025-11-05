package com.tcc.desperdicio_alimentos.repository;


import com.tcc.desperdicio_alimentos.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {
    List<Doacao> findByStatus(StatusDoacao status);
    List<Doacao> findByOngAndStatus(Ong ong, StatusDoacao status);
}
