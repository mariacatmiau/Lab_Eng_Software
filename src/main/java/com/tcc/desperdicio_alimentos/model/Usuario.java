package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String tipo; // "FUNCIONARIO" ou "ONG"

    private String endereco; // usado apenas se for ONG
    private String telefone; // usado apenas se for ONG
}
