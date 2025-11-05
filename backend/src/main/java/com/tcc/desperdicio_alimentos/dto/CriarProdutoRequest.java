package com.tcc.desperdicio_alimentos.dto;

import java.time.LocalDate;

public class CriarProdutoRequest {
    public String nome;
    public String descricao;
    public LocalDate dataValidade;
    public Integer quantidade;
    public Long funcionarioId;
}
