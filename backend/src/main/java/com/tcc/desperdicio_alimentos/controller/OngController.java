package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Ong;
import com.tcc.desperdicio_alimentos.repository.OngRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/ongs")
@CrossOrigin
public class OngController {

    private final OngRepository repo;
    public OngController(OngRepository r) { this.repo = r; }

    @GetMapping
    public ResponseEntity<List<Ong>> listar() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ong> get(@PathVariable Long id) {
        return ResponseEntity.of(repo.findById(id));
    }
}
