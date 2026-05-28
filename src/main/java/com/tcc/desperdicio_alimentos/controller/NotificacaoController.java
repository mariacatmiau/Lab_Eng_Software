package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Notificacao;
import com.tcc.desperdicio_alimentos.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public ResponseEntity<?> listar(Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        List<Notificacao> lista = notificacaoService.listar(usuarioId);
        List<Map<String, Object>> resultado = lista.stream()
                .map(n -> Map.<String, Object>of(
                        "id", n.getId(),
                        "mensagem", n.getMensagem(),
                        "lida", n.isLida(),
                        "data", n.getDataCriacao().format(FMT)
                ))
                .toList();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/count")
    public ResponseEntity<?> count(Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(Map.of("naoLidas", notificacaoService.contarNaoLidas(usuarioId)));
    }

    @PutMapping("/{notifId}/lida")
    public ResponseEntity<?> marcarLida(@PathVariable Long notifId, Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        notificacaoService.marcarComoLida(notifId, usuarioId);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PutMapping("/todas-lidas")
    public ResponseEntity<?> marcarTodasLidas(Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        notificacaoService.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
