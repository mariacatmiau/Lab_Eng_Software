package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.FinalizarPedidoRequest;
import com.tcc.desperdicio_alimentos.dto.PedidoResumoDTO;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.PedidoItemRepository;
import com.tcc.desperdicio_alimentos.repository.PedidoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PedidoServiceTest {

    private PedidoService service;
    private PedidoRepository pedidoRepository;
    private PedidoItemRepository pedidoItemRepository;
    private ProdutoRepository produtoRepository;
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setup() {
        pedidoRepository = mock(PedidoRepository.class);
        pedidoItemRepository = mock(PedidoItemRepository.class);
        produtoRepository = mock(ProdutoRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        service = new PedidoService(pedidoRepository, pedidoItemRepository, produtoRepository, usuarioRepository);
    }

    @Test
    void deveFinalizarPedidoEBaixarEstoque() {
        Usuario cliente = new Usuario();
        cliente.setId(30L);
        cliente.setTipo(UsuarioTipo.CLIENTE);

        Usuario mercado = new Usuario();
        mercado.setId(10L);
        mercado.setNome("Mercado Centro");
        mercado.setEndereco("Rua A, 10");
        mercado.setTelefone("11999999999");
        mercado.setTipo(UsuarioTipo.FUNCIONARIO);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz");
        produto.setQuantidade(5);
        produto.setDisponivel(true);
        produto.setTipoOferta(TipoOfertaProduto.VENDA);
        produto.setPreco(new BigDecimal("12.50"));
        produto.setCriadoPor(mercado);

        FinalizarPedidoRequest request = new FinalizarPedidoRequest();
        FinalizarPedidoRequest.ItemPedidoRequest item = new FinalizarPedidoRequest.ItemPedidoRequest();
        item.produtoId = 1L;
        item.quantidade = 2;
        request.itens = List.of(item);

        when(usuarioRepository.findById(30L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            if (pedido.getId() == null) {
                pedido.setId(100L);
            }
            return pedido;
        });

        PedidoResumoDTO resumo = service.finalizarPedido(30L, request);

        assertEquals(100L, resumo.pedidoId);
        assertEquals(new BigDecimal("25.00"), resumo.valorTotal);
        assertEquals(1, resumo.mercados.size());
        assertEquals("Mercado Centro", resumo.mercados.get(0).mercadoNome);
        assertEquals(3, produto.getQuantidade());
        assertTrue(produto.getDisponivel());
        verify(pedidoItemRepository).saveAll(anyList());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveFalharQuandoEstoqueForInsuficiente() {
        Usuario cliente = new Usuario();
        cliente.setId(30L);
        cliente.setTipo(UsuarioTipo.CLIENTE);

        Usuario mercado = new Usuario();
        mercado.setId(10L);
        mercado.setTipo(UsuarioTipo.FUNCIONARIO);

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Arroz");
        produto.setQuantidade(1);
        produto.setDisponivel(true);
        produto.setTipoOferta(TipoOfertaProduto.VENDA);
        produto.setPreco(new BigDecimal("12.50"));
        produto.setCriadoPor(mercado);

        FinalizarPedidoRequest request = new FinalizarPedidoRequest();
        FinalizarPedidoRequest.ItemPedidoRequest item = new FinalizarPedidoRequest.ItemPedidoRequest();
        item.produtoId = 1L;
        item.quantidade = 3;
        request.itens = List.of(item);

        when(usuarioRepository.findById(30L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(ResponseStatusException.class, () -> service.finalizarPedido(30L, request));
        verify(pedidoItemRepository, never()).saveAll(anyList());
    }
}