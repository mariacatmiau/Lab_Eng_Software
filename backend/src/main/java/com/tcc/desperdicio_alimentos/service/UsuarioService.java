package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Cadastrar novo usuário (ONG ou Funcionário)
    public Usuario cadastrar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Listar todos os usuários
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // Buscar usuário por e-mail
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Login simples (valida e-mail e senha)
    public boolean login(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        return usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha);
    }
}
