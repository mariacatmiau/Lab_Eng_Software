package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    List<PedidoItem> findByPedidoId(Long pedidoId);
    List<PedidoItem> findByMercadoIdAndPedidoStatus(Long mercadoId, com.tcc.desperdicio_alimentos.model.StatusPedido status);
    boolean existsByPedidoIdAndMercadoId(Long pedidoId, Long mercadoId);
}