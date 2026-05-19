package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.model.Notificacao;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.NotificacaoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepo;
    private final UsuarioRepository usuarioRepo;

    public NotificacaoService(NotificacaoRepository notificacaoRepo, UsuarioRepository usuarioRepo) {
        this.notificacaoRepo = notificacaoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public void criar(Long usuarioId, String mensagem) {
        Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);
        if (usuario == null) return;

        Notificacao n = new Notificacao();
        n.setUsuario(usuario);
        n.setMensagem(mensagem);
        notificacaoRepo.save(n);
    }

    public List<Notificacao> listar(Long usuarioId) {
        return notificacaoRepo.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
    }

    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepo.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    public void marcarComoLida(Long notifId, Long usuarioId) {
        Notificacao n = notificacaoRepo.findById(notifId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));
        if (!n.getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        n.setLida(true);
        notificacaoRepo.save(n);
    }

    public void marcarTodasComoLidas(Long usuarioId) {
        notificacaoRepo.marcarTodasComoLidasPorUsuario(usuarioId);
    }
}
