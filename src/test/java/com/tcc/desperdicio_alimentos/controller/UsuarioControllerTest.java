package com.tcc.desperdicio_alimentos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.model.UsuarioTipo;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import com.tcc.desperdicio_alimentos.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockBean UsuarioService usuarioService;
    @MockBean UsuarioRepository usuarioRepository;

    @Test
    void deveFazerLogin() throws Exception {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setEmail("a@a.com");
        u.setSenha("123");
        u.setTipo(UsuarioTipo.FUNCIONARIO);

        when(usuarioRepository.findByEmail("a@a.com")).thenReturn(Optional.of(u));

        Map<String, String> body = new HashMap<>();
        body.put("email", "a@a.com");
        body.put("senha", "123");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveFalhar_LoginEmailNaoEncontrado() throws Exception {
        when(usuarioRepository.findByEmail("x@x.com")).thenReturn(Optional.empty());

        Map<String, String> body = new HashMap<>();
        body.put("email", "x@x.com");
        body.put("senha", "123");

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
