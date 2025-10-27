package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Cadastro
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado!");
        }

        Usuario user = existingUser.get();
        if (passwordEncoder.matches(usuario.getSenha(), user.getSenha())) {
            return ResponseEntity.ok("Login realizado com sucesso! Bem-vindo(a), " + user.getNome() + "!");
        } else {
            return ResponseEntity.badRequest().body("Senha incorreta!");
        }
    }
}
