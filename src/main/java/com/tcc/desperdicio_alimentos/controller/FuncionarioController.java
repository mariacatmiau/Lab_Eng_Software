package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Funcionario;
import com.tcc.desperdicio_alimentos.repository.FuncionarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity<Funcionario> buscarPorUsuario(@PathVariable Long usuarioId) {
        Optional<Funcionario> funcionario = funcionarioRepository.findByUsuarioId(usuarioId);
        return funcionario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(funcionarioRepository.findAll());
    }
}
