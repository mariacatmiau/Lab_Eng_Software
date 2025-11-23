package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.model.UsuarioTipo;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ongs")
@CrossOrigin(origins = "*")
public class OngController {

    private final UsuarioRepository usuarioRepo;

    public OngController(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    // Lista todas as ONGs cadastradas (usuários com tipo = ONG)
    @GetMapping
    public ResponseEntity<List<Usuario>> listarOngs() {
        List<Usuario> ongs = usuarioRepo.findAll()
                .stream()
                .filter(u -> u.getTipo() == UsuarioTipo.ONG)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ongs);
    }
}
