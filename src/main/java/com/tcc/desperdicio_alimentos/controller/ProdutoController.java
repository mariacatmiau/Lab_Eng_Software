package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    // Listar todos (caso precise para debug)
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Criar produto
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody CriarProdutoRequest req) {
        return ResponseEntity.ok(service.criar(req));
    }

    // Listar produtos disponíveis (para ONG escolher, por exemplo)
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Produto>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    // Listar produtos por usuário (para funcionário ver o que ele cadastrou)
    @GetMapping("/por-usuario/{id}")
    public ResponseEntity<List<Produto>> listarPorUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorUsuario(id));
    }

    // Marcar produto como indisponível
    @PutMapping("/{id}/indisponivel")
    public ResponseEntity<Produto> marcarIndisponivel(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarIndisponivel(id));
    }
}
