package com.tcc.desperdicio_alimentos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    @Enumerated(EnumType.STRING)
    private UsuarioTipo tipo; // FUNCIONARIO ou ONG

    private String endereco; // usado apenas se for ONG
    private String telefone; // usado apenas se for ONG
    private Double latitude;
    private Double longitude;
}
