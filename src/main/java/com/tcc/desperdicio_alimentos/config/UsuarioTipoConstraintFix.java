package com.tcc.desperdicio_alimentos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UsuarioTipoConstraintFix {

    private static final Logger log = LoggerFactory.getLogger(UsuarioTipoConstraintFix.class);

    private final JdbcTemplate jdbcTemplate;

    public UsuarioTipoConstraintFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fixUsuarioTipoConstraint() {
        try {
            jdbcTemplate.execute("ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_tipo_check");
            jdbcTemplate.execute("ALTER TABLE usuarios ADD CONSTRAINT usuarios_tipo_check CHECK (tipo IN ('FUNCIONARIO','ONG','CLIENTE'))");
            log.info("Constraint usuarios_tipo_check atualizada com CLIENTE.");
        } catch (Exception ex) {
            // Tabela pode não existir em algum ciclo de teste/startup; não deve interromper a aplicação.
            log.debug("Não foi possível atualizar constraint usuarios_tipo_check: {}", ex.getMessage());
        }
    }
}
