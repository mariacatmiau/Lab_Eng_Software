package com.tcc.desperdicio_alimentos.dto;

public class LoginResponseDTO {
    public String token;
    public UsuarioResumoDTO usuario;

    public LoginResponseDTO(String token, UsuarioResumoDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }
}
