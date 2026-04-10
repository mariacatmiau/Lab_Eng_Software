package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}