package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "doacoes")
public class Doacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Ong ong;

    @OneToOne(optional = false)
    private Produto produto;

    @ManyToOne(optional = false)
    private Funcionario criadoPor;

    @Enumerated(EnumType.STRING)
    private StatusDoacao status = StatusDoacao.PENDENTE;
    private Integer quantidade;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataAceite;
    private LocalDateTime dataRetirada;
    private String motivoRecusa;
}
