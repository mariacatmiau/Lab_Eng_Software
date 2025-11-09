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

    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "ong_id", nullable = false)
    private Usuario ong;

    @ManyToOne
    @JoinColumn(name = "criado_por_id", nullable = false) // ✅ novo campo
    private Usuario criadoPor;

    @Enumerated(EnumType.STRING)
    private StatusDoacao status = StatusDoacao.PENDENTE;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}
