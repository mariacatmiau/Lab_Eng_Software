package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.CriarDoacaoRequest;
import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.service.DoacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doacoes")
@CrossOrigin(origins = "*")
public class DoacaoController {

    private final DoacaoService service;

    public DoacaoController(DoacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Doacao> criar(@RequestBody CriarDoacaoRequest req) {
        return ResponseEntity.ok(service.criar(req));
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<Doacao> aceitar(@PathVariable Long id) {
        return ResponseEntity.ok(service.aceitar(id));
    }

    @PutMapping("/{id}/recusar")
    public ResponseEntity<Doacao> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(service.recusar(id));
    }

    @PutMapping("/{id}/retirada")
    public ResponseEntity<Doacao> retirada(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmarRetirada(id));
    }

    @GetMapping
    public ResponseEntity<List<Doacao>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/por-ong/{id}")
    public ResponseEntity<List<Doacao>> listarPorOng(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorOng(id));
    }
}
