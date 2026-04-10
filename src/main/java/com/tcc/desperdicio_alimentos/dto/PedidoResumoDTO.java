package com.tcc.desperdicio_alimentos.dto;

import com.tcc.desperdicio_alimentos.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoResumoDTO {
    public Long pedidoId;
    public String status;
    public BigDecimal valorTotal;
    public LocalDateTime criadoEm;
    public List<PedidoMercadoResumoDTO> mercados = new ArrayList<>();

    public static PedidoResumoDTO from(Pedido pedido) {
        PedidoResumoDTO dto = new PedidoResumoDTO();
        dto.pedidoId = pedido.getId();
        dto.status = pedido.getStatus() != null ? pedido.getStatus().name() : null;
        dto.valorTotal = pedido.getValorTotal();
        dto.criadoEm = pedido.getCriadoEm();
        return dto;
    }

    public static class PedidoMercadoResumoDTO {
        public Long mercadoId;
        public String mercadoNome;
        public String mercadoTelefone;
        public String mercadoEndereco;
        public BigDecimal total;
        public String mensagemWhatsapp;
        public List<PedidoItemResumoDTO> itens = new ArrayList<>();
    }

    public static class PedidoItemResumoDTO {
        public Long produtoId;
        public String produtoNome;
        public Integer quantidade;
        public BigDecimal precoUnitario;
        public BigDecimal subtotal;
    }
}