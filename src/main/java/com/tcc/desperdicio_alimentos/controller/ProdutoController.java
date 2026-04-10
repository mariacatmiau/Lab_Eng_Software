package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.dto.OfertaProdutoDTO;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<Produto> criar(@RequestBody CriarProdutoRequest req, Authentication authentication) {
        req.usuarioId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(service.criar(req));
    }

    // Listar produtos disponíveis (para ONG escolher, por exemplo)
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Produto>> listarDisponiveis() {
        return ResponseEntity.ok(service.listarDisponiveis());
    }

    // Listar ofertas para clientes, com busca por produto/mercado e referência de proximidade
    @GetMapping("/ofertas")
    public ResponseEntity<List<OfertaProdutoDTO>> listarOfertas(
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "PRODUTO") String tipoBusca,
            @RequestParam(required = false) String referencia,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        return ResponseEntity.ok(service.listarOfertas(busca, tipoBusca, referencia, latitude, longitude));
    }

    // Listar produtos por usuário (para funcionário ver o que ele cadastrou)
    @GetMapping("/por-usuario/{id}")
    public ResponseEntity<List<Produto>> listarPorUsuario(@PathVariable Long id, Authentication authentication) {
        if (Long.parseLong(authentication.getName()) != id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(service.listarPorUsuario(id));
    }

    // Marcar produto como indisponível
    @PutMapping("/{id}/indisponivel")
    public ResponseEntity<Produto> marcarIndisponivel(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(service.marcarIndisponivel(id, usuarioId));
    }
}
