package com.tcc.desperdicio_alimentos.dto;

public class CriarDoacaoRequest {
    public Long produtoId;
    public Long ongId;
    public Integer quantidade;
    public Long criadoPorId; // ✅ funcionário que criou a doação
}
