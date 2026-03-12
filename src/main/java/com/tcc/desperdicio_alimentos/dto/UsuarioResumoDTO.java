package com.tcc.desperdicio_alimentos.dto;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.model.UsuarioTipo;

public class UsuarioResumoDTO {
    public Long id;
    public String nome;
    public String email;
    public UsuarioTipo tipo;
    public String endereco;
    public String telefone;
    public Double latitude;
    public Double longitude;

    public static UsuarioResumoDTO from(Usuario usuario) {
        UsuarioResumoDTO dto = new UsuarioResumoDTO();
        dto.id = usuario.getId();
        dto.nome = usuario.getNome();
        dto.email = usuario.getEmail();
        dto.tipo = usuario.getTipo();
        dto.endereco = usuario.getEndereco();
        dto.telefone = usuario.getTelefone();
        dto.latitude = usuario.getLatitude();
        dto.longitude = usuario.getLongitude();
        return dto;
    }
}
