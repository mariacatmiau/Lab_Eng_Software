package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.FinalizarPedidoRequest;
import com.tcc.desperdicio_alimentos.dto.PedidoResumoDTO;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.PedidoItemRepository;
import com.tcc.desperdicio_alimentos.repository.PedidoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, PedidoItemRepository pedidoItemRepository,
                         ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PedidoResumoDTO finalizarPedido(Long clienteId, FinalizarPedidoRequest request) {
        if (request == null || request.itens == null || request.itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie ao menos um item para finalizar o pedido");
        }

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        if (cliente.getTipo() != UsuarioTipo.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas clientes podem finalizar pedidos");
        }

        Map<Long, Integer> quantidadesPorProduto = consolidarItens(request.itens);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setCriadoEm(LocalDateTime.now());
        pedido.setValorTotal(BigDecimal.ZERO);
        pedido = pedidoRepository.save(pedido);

        List<PedidoItem> itensPedido = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;
        Map<Long, PedidoResumoDTO.PedidoMercadoResumoDTO> mercados = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : quantidadesPorProduto.entrySet()) {
            Produto produto = produtoRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + entry.getKey()));

            validarProdutoParaVenda(produto);

            Integer quantidadeSolicitada = entry.getValue();
            Integer quantidadeAtual = produto.getQuantidade() != null ? produto.getQuantidade() : 0;
            if (quantidadeSolicitada > quantidadeAtual) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Estoque insuficiente para " + produto.getNome() + ". Disponível: " + quantidadeAtual
                );
            }

            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidadeSolicitada.longValue()));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setMercado(produto.getCriadoPor());
            item.setProdutoNome(produto.getNome());
            item.setQuantidade(quantidadeSolicitada);
            item.setPrecoUnitario(precoUnitario);
            item.setSubtotal(subtotal);
            itensPedido.add(item);

            int quantidadeRestante = quantidadeAtual - quantidadeSolicitada;
            produto.setQuantidade(quantidadeRestante);
            produto.setDisponivel(quantidadeRestante > 0);
            produtoRepository.save(produto);

            valorTotal = valorTotal.add(subtotal);
            adicionarResumoMercado(mercados, item);
        }

        pedidoItemRepository.saveAll(itensPedido);
        pedido.setValorTotal(valorTotal);
        pedidoRepository.save(pedido);

        PedidoResumoDTO resumo = PedidoResumoDTO.from(pedido);
        resumo.mercados.addAll(mercados.values());
        return resumo;
    }

    private Map<Long, Integer> consolidarItens(List<FinalizarPedidoRequest.ItemPedidoRequest> itens) {
        Map<Long, Integer> quantidades = new LinkedHashMap<>();

        for (FinalizarPedidoRequest.ItemPedidoRequest item : itens) {
            if (item == null || item.produtoId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cada item do pedido deve informar o produto");
            }

            int quantidade = item.quantidade != null ? item.quantidade : 0;
            if (quantidade <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade de cada item deve ser maior que zero");
            }

            quantidades.merge(item.produtoId, quantidade, Integer::sum);
        }

        return quantidades;
    }

    private void validarProdutoParaVenda(Produto produto) {
        if (!Boolean.TRUE.equals(produto.getDisponivel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O produto " + produto.getNome() + " não está mais disponível");
        }

        if (produto.getTipoOferta() != TipoOfertaProduto.VENDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto " + produto.getNome() + " não pertence ao catálogo de vendas");
        }

        if (produto.getCriadoPor() == null || produto.getCriadoPor().getTipo() != UsuarioTipo.FUNCIONARIO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto " + produto.getNome() + " não está vinculado a um mercado válido");
        }

        if (produto.getPreco() == null || produto.getPreco().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto " + produto.getNome() + " está sem preço válido para venda");
        }
    }

    private void adicionarResumoMercado(Map<Long, PedidoResumoDTO.PedidoMercadoResumoDTO> mercados, PedidoItem item) {
        Usuario mercado = item.getMercado();
        PedidoResumoDTO.PedidoMercadoResumoDTO resumoMercado = mercados.computeIfAbsent(mercado.getId(), ignored -> {
            PedidoResumoDTO.PedidoMercadoResumoDTO dto = new PedidoResumoDTO.PedidoMercadoResumoDTO();
            dto.mercadoId = mercado.getId();
            dto.mercadoNome = mercado.getNome();
            dto.mercadoTelefone = mercado.getTelefone();
            dto.mercadoEndereco = mercado.getEndereco();
            dto.total = BigDecimal.ZERO;
            dto.mensagemWhatsapp = "";
            return dto;
        });

        PedidoResumoDTO.PedidoItemResumoDTO itemResumo = new PedidoResumoDTO.PedidoItemResumoDTO();
        itemResumo.produtoId = item.getProduto().getId();
        itemResumo.produtoNome = item.getProdutoNome();
        itemResumo.quantidade = item.getQuantidade();
        itemResumo.precoUnitario = item.getPrecoUnitario();
        itemResumo.subtotal = item.getSubtotal();
        resumoMercado.itens.add(itemResumo);
        resumoMercado.total = resumoMercado.total.add(item.getSubtotal());
        resumoMercado.mensagemWhatsapp = gerarMensagemWhatsapp(resumoMercado);
    }

    private String gerarMensagemWhatsapp(PedidoResumoDTO.PedidoMercadoResumoDTO mercado) {
        List<String> linhas = new ArrayList<>();
        linhas.add("Olá, " + valorOuPadrao(mercado.mercadoNome, "mercado") + ".");
        linhas.add("Acabei de registrar meu pedido na DoaDoa e gostaria de combinar o pagamento via Pix:");
        linhas.add("");

        for (PedidoResumoDTO.PedidoItemResumoDTO item : mercado.itens) {
            linhas.add(String.format(
                    Locale.ROOT,
                    "- %s | qtd: %d | unitário: R$ %s | subtotal: R$ %s",
                    valorOuPadrao(item.produtoNome, "Produto"),
                    item.quantidade,
                    item.precoUnitario != null ? item.precoUnitario.toPlainString() : "0.00",
                    item.subtotal != null ? item.subtotal.toPlainString() : "0.00"
            ));
        }

        linhas.add("");
        linhas.add("Total do pedido: R$ " + (mercado.total != null ? mercado.total.toPlainString() : "0.00"));
        linhas.add("Pode me enviar a chave Pix e orientar a retirada, por favor?");
        return String.join("\n", linhas);
    }

    private String valorOuPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}