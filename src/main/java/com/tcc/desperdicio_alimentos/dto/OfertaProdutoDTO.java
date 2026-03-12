package com.tcc.desperdicio_alimentos.dto;

import com.tcc.desperdicio_alimentos.model.Produto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class OfertaProdutoDTO {
    public Long produtoId;
    public String produtoNome;
    public String categoria;
    public Integer quantidade;
    public BigDecimal preco;
    public LocalDate dataValidade;
    public Long diasParaVencer;
    public Long mercadoId;
    public String mercadoNome;
    public String mercadoEndereco;
    public String mercadoTelefone;
    public Double distanciaKm;
    public Double mercadoLatitude;
    public Double mercadoLongitude;

    public static OfertaProdutoDTO from(Produto produto) {
        OfertaProdutoDTO dto = new OfertaProdutoDTO();
        dto.produtoId = produto.getId();
        dto.produtoNome = produto.getNome();
        dto.categoria = produto.getCategoria();
        dto.quantidade = produto.getQuantidade();
        dto.preco = produto.getPreco();
        dto.dataValidade = produto.getDataValidade();
        dto.diasParaVencer = produto.getDataValidade() != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), produto.getDataValidade())
                : null;

        if (produto.getCriadoPor() != null) {
            dto.mercadoId = produto.getCriadoPor().getId();
            dto.mercadoNome = produto.getCriadoPor().getNome();
            dto.mercadoEndereco = produto.getCriadoPor().getEndereco();
            dto.mercadoTelefone = produto.getCriadoPor().getTelefone();
            dto.mercadoLatitude = produto.getCriadoPor().getLatitude();
            dto.mercadoLongitude = produto.getCriadoPor().getLongitude();
        }

        return dto;
    }
}
