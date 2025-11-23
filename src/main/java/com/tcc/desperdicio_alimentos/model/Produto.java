package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario criadoPor;
}
