package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo; // ONG ou MERCADO

    public enum TipoUsuario {
        ONG,
        MERCADO
    }
}
