package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Funcionario;
import com.tcc.desperdicio_alimentos.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuncionarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FuncionarioControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean FuncionarioRepository repo;

    @Test
    void deveBuscarFuncionarioPorUsuario() throws Exception {
        Funcionario f = new Funcionario();
        f.setId(1L);

        when(repo.findByUsuarioId(10L)).thenReturn(Optional.of(f));

        mockMvc.perform(get("/api/funcionarios/por-usuario/10"))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar404QuandoNaoEncontrado() throws Exception {
        when(repo.findByUsuarioId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/funcionarios/por-usuario/99"))
                .andExpect(status().isNotFound());
    }
}
