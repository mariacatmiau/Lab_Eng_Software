package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByUsuarioId(Long usuarioId);
}
