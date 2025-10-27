package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*") // permite acesso do front
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ✅ Cadastrar um novo usuário
    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.cadastrar(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    // ✅ Listar todos os usuários
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.listar();
        return ResponseEntity.ok(usuarios);
    }

    // ✅ Buscar usuário por e-mail
    @GetMapping("/buscar")
    public ResponseEntity<Optional<Usuario>> buscarPorEmail(@RequestParam String email) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Fazer login simples (sem autenticação JWT por enquanto)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String senha) {
        boolean autenticado = usuarioService.login(email, senha);
        if (autenticado) {
            return ResponseEntity.ok("Login realizado com sucesso!");
        } else {
            return ResponseEntity.status(401).body("E-mail ou senha incorretos!");
        }
    }
}
