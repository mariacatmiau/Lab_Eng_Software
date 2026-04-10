package com.tcc.desperdicio_alimentos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.desperdicio_alimentos.dto.FinalizarPedidoRequest;
import com.tcc.desperdicio_alimentos.dto.PedidoResumoDTO;
import com.tcc.desperdicio_alimentos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PedidoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PedidoService pedidoService;

    @Autowired
    ObjectMapper mapper;

    @Test
    void deveFinalizarPedido() throws Exception {
        FinalizarPedidoRequest request = new FinalizarPedidoRequest();
        FinalizarPedidoRequest.ItemPedidoRequest item = new FinalizarPedidoRequest.ItemPedidoRequest();
        item.produtoId = 1L;
        item.quantidade = 2;
        request.itens = List.of(item);

        PedidoResumoDTO resposta = new PedidoResumoDTO();
        resposta.pedidoId = 99L;
        resposta.status = "AGUARDANDO_PAGAMENTO";
        resposta.valorTotal = new BigDecimal("40.00");

        when(pedidoService.finalizarPedido(eq(7L), any(FinalizarPedidoRequest.class))).thenReturn(resposta);

        mockMvc.perform(post("/api/pedidos")
                        .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("7", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(99))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }
}