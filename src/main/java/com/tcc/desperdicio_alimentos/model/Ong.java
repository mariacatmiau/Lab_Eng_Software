package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Ong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String endereco;

    @OneToMany(mappedBy = "ong")
    private List<Produto> produtosRecebidos;
}
