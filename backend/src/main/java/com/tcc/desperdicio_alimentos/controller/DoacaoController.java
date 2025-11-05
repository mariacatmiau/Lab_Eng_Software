package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.CriarDoacaoRequest;
import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.service.DoacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doacoes")
@CrossOrigin
public class DoacaoController {

    private final DoacaoService service;
    public DoacaoController(DoacaoService s) { this.service = s; }

    @PostMapping
    public ResponseEntity<Doacao> criar(@RequestBody CriarDoacaoRequest req) {
        return ResponseEntity.ok(service.criar(req));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Doacao>> pendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }

    @GetMapping("/pendentes/ong/{ongId}")
    public ResponseEntity<List<Doacao>> pendentesDaOng(@PathVariable Long ongId) {
        return ResponseEntity.ok(service.listarPendentesDaOng(ongId));
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<Doacao> aceitar(@PathVariable Long id) {
        return ResponseEntity.ok(service.aceitar(id));
    }

    @PutMapping("/{id}/recusar")
    public ResponseEntity<Doacao> recusar(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(service.recusar(id, motivo));
    }

    @PutMapping("/{id}/retirar")
    public ResponseEntity<Doacao> retirar(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarRetirada(id));
    }
}
