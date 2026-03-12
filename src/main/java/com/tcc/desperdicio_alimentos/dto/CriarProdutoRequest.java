package com.tcc.desperdicio_alimentos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CriarProdutoRequest {
    public String nome;
    public String categoria;
    public LocalDate dataValidade;
    public Integer quantidade;
    public Long usuarioId;
    public String tipoOferta;
    public BigDecimal preco;
    public Long ongId;
}
