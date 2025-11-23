package com.tcc.desperdicio_alimentos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.desperdicio_alimentos.dto.CriarDoacaoRequest;
import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.service.DoacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DoacaoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    DoacaoService service;

    @Autowired
    ObjectMapper mapper;

    @Test
    void deveCriarDoacao() throws Exception {
        CriarDoacaoRequest req = new CriarDoacaoRequest();
        req.produtoId = 1L;
        req.ongId = 2L;
        req.criadoPorId = 3L;

        Doacao d = new Doacao();
        d.setId(10L);

        when(service.criar(any())).thenReturn(d);

        mockMvc.perform(post("/api/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deveAceitarDoacao() throws Exception {
        Doacao d = new Doacao();
        d.setId(1L);

        when(service.aceitar(1L)).thenReturn(d);

        mockMvc.perform(put("/api/doacoes/1/aceitar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveListarTodas() throws Exception {
        when(service.listarTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/doacoes"))
                .andExpect(status().isOk());
    }
}
