package com.tcc.desperdicio_alimentos.repository;

import com.tcc.desperdicio_alimentos.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

    long countByUsuarioIdAndLidaFalse(Long usuarioId);

    @Modifying
    @Transactional
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.usuario.id = :usuarioId")
    void marcarTodasComoLidasPorUsuario(Long usuarioId);
}
