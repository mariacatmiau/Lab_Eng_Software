package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Ong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OngRepository extends JpaRepository<Ong, Long> {
    Optional<Ong> findByUsuarioId(Long usuarioId);
}
