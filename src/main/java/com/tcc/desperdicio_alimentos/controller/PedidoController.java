package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.FinalizarPedidoRequest;
import com.tcc.desperdicio_alimentos.dto.PedidoResumoDTO;
import com.tcc.desperdicio_alimentos.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}