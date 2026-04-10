package com.tcc.desperdicio_alimentos.dto;

import java.util.List;

public class FinalizarPedidoRequest {
    public List<ItemPedidoRequest> itens;

    public static class ItemPedidoRequest {
        public Long produtoId;
        public Integer quantidade;
    }
}