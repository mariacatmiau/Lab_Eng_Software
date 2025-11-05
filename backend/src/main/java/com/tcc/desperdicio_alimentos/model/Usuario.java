package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String nome;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String senha; // para produção: hash (BCrypt)

    @Enumerated(EnumType.STRING)
    private UsuarioTipo tipo; // FUNCIONARIO ou ONG

    // getters/setters
    // ...
}
