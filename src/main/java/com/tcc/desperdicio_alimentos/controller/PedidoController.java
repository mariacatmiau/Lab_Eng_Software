package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.FinalizarPedidoRequest;
import com.tcc.desperdicio_alimentos.dto.PedidoResumoDTO;
import com.tcc.desperdicio_alimentos.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResumoDTO> finalizarPedido(@RequestBody FinalizarPedidoRequest request,
                                                           Authentication authentication) {
        Long clienteId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(pedidoService.finalizarPedido(clienteId, request));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PedidoResumoDTO>> listarMeusPedidos(Authentication authentication) {
        Long clienteId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(pedidoService.listarMeusPedidos(clienteId));
    }

    @GetMapping("/por-mercado")
    public ResponseEntity<List<PedidoResumoDTO>> listarPorMercado(Authentication authentication) {
        Long mercadoId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(pedidoService.listarPedidosPorMercado(mercadoId));
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<Void> confirmarPagamento(@PathVariable Long id, Authentication authentication) {
        Long mercadoId = Long.parseLong(authentication.getName());
        pedidoService.confirmarPagamento(id, mercadoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id, Authentication authentication) {
        Long clienteId = Long.parseLong(authentication.getName());
        pedidoService.cancelarPedido(id, clienteId);
        return ResponseEntity.ok().build();
    }
}