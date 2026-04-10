package com.tcc.desperdicio_alimentos.dto;

import java.util.List;

public class DashboardImpactoDTO {

    public long totalDoacoes;
    public long doacoesPendentes;
    public long doacoesAceitas;
    public long doacoesRetiradas;
    public long doacoesRecusadas;
    public long totalProdutosCadastrados;
    public long produtosDisponiveis;
    public long totalOngs;
    public long totalMercados;
    public long totalClientes;
    public long totalPedidos;
    public long itensDoados;
    public List<RankingOngDTO> rankingOngs;
    public List<RankingMercadoDTO> rankingMercados;
    public List<DoacaoRecenteDTO> doacoesRecentes;

    public static class RankingOngDTO {
        public Long id;
        public String nome;
        public long retiradasConcluidas;
        public long totalRecebidas;

        public RankingOngDTO(Long id, String nome, long retiradasConcluidas, long totalRecebidas) {
            this.id = id;
            this.nome = nome;
            this.retiradasConcluidas = retiradasConcluidas;
            this.totalRecebidas = totalRecebidas;
        }
    }

    public static class RankingMercadoDTO {
        public Long id;
        public String nome;
        public long totalDoacoes;
        public long itensDoados;

        public RankingMercadoDTO(Long id, String nome, long totalDoacoes, long itensDoados) {
            this.id = id;
            this.nome = nome;
            this.totalDoacoes = totalDoacoes;
            this.itensDoados = itensDoados;
        }
    }

    public static class DoacaoRecenteDTO {
        public Long id;
        public String produto;
        public String mercado;
        public String ong;
        public String status;
        public String data;

        public DoacaoRecenteDTO(Long id, String produto, String mercado, String ong, String status, String data) {
            this.id = id;
            this.produto = produto;
            this.mercado = mercado;
            this.ong = ong;
            this.status = status;
            this.data = data;
        }
    }
}
