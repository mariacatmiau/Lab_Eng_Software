package com.tcc.desperdicio_alimentos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.dto.OfertaProdutoDTO;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProdutoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProdutoService service;

    @Autowired
    ObjectMapper mapper;

    @Test
    void deveCriarProduto() throws Exception {
        CriarProdutoRequest req = new CriarProdutoRequest();
        req.nome = "Arroz";
        req.categoria = "Alimento";
        req.dataValidade = LocalDate.now();
        req.quantidade = 5;
        req.usuarioId = 1L;

        Produto p = new Produto();
        p.setId(10L);
        p.setNome("Arroz");

        when(service.criar(any())).thenReturn(p);

        mockMvc.perform(post("/api/produtos")
            .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("1", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deveListarTodos() throws Exception {
        when(service.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarDisponiveis() throws Exception {
        when(service.listarDisponiveis()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/produtos/disponiveis"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarOfertasParaCliente() throws Exception {
        OfertaProdutoDTO oferta = new OfertaProdutoDTO();
        oferta.produtoId = 1L;
        oferta.produtoNome = "Arroz";
        oferta.mercadoNome = "Mercado Centro";

        when(service.listarOfertas("arroz", "PRODUTO", "centro", -23.55, -46.63))
                .thenReturn(List.of(oferta));

        mockMvc.perform(get("/api/produtos/ofertas")
                        .param("busca", "arroz")
                        .param("tipoBusca", "PRODUTO")
                .param("referencia", "centro")
                .param("latitude", "-23.55")
                .param("longitude", "-46.63"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].produtoId").value(1))
                .andExpect(jsonPath("$[0].mercadoNome").value("Mercado Centro"));
    }
}
