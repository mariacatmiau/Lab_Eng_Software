package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    boolean existsByClienteIdAndStatus(Long clienteId, com.tcc.desperdicio_alimentos.model.StatusPedido status);
}