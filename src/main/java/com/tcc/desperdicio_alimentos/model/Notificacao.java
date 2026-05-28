package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @Entity @Table(name = "notificacoes")
public class Notificacao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 500)
    private String mensagem;

    private boolean lida = false;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}
