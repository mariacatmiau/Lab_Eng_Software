package com.tcc.desperdicio_alimentos.dto;

import com.tcc.desperdicio_alimentos.model.UsuarioTipo;

public class RegisterRequest {
    public String nome;
    public String email;
    public String senha;
    public UsuarioTipo tipo; // FUNCIONARIO ou ONG

    // Campos específicos de ONG
    public String cnpj;
    public String endereco;
    public String telefone;
}
