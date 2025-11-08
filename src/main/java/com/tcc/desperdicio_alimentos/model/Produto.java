package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank
    private String nome;

    private String categoria;

    @NotNull
    private LocalDate dataValidade;

    @NotNull
    private Integer quantidade;

    private boolean disponivel = true;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario criadoPor;

}
