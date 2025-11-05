package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> { }
