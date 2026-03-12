package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private LocalDate dataValidade;
    private Integer quantidade;
    private Boolean disponivel = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOfertaProduto tipoOferta = TipoOfertaProduto.DOACAO;

    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario criadoPor;

    @ManyToOne
    @JoinColumn(name = "ong_destino_id")
    private Usuario ongDestino;
}
