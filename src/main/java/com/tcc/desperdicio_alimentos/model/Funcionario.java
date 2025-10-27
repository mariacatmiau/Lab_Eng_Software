package com.tcc.desperdicio_alimentos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;

    @OneToMany(mappedBy = "funcionario")
    private List<Produto> produtosCadastrados;
}
