package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate dataValidade;
    private Integer quantidade;
    private Double preco;

    private boolean doado = false;

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "ong_id", nullable = true)
    private Ong ong; // preenchido quando for doado
}
