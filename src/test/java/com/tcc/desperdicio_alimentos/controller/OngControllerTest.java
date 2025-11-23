package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.model.UsuarioTipo;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OngController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OngControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean UsuarioRepository repo;

    @Test
    void deveListarOngs() throws Exception {
        Usuario u = new Usuario();
        u.setTipo(UsuarioTipo.ONG);

        when(repo.findAll()).thenReturn(Arrays.asList(u));

        mockMvc.perform(get("/api/ongs"))
                .andExpect(status().isOk());
    }
}
