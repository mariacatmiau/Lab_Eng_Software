package com.tcc.desperdicio_alimentos.controller;


import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin
public class ProdutoController {

    private final ProdutoService service;
    public ProdutoController(ProdutoService s) { this.service = s; }

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody CriarProdutoRequest req) {
        return ResponseEntity.ok(service.criar(req));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Produto>> disponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    @GetMapping("/proximos-vencimento")
    public ResponseEntity<List<Produto>> proximos(@RequestParam(defaultValue = "7") int dias) {
        return ResponseEntity.ok(service.proximosVencimento(dias));
    }

    @PutMapping("/{id}/indisponivel")
    public ResponseEntity<Produto> indisponivel(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarIndisponivel(id));
    }
}
