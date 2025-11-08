package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepo;

    public UsuarioController(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    // Registro
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario u) {
        if (usuarioRepo.existsByEmail(u.getEmail())) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado.");
        }
        usuarioRepo.save(u);
        return ResponseEntity.ok(u);
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario login) {
        Optional<Usuario> usuario = usuarioRepo.findByEmail(login.getEmail());
        if (usuario.isPresent() && usuario.get().getSenha().equals(login.getSenha())) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(401).body("Credenciais inválidas.");
    }
}
