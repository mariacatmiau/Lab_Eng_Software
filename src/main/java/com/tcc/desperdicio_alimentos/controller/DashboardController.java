package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.DashboardImpactoDTO;
import com.tcc.desperdicio_alimentos.dto.DashboardImpactoDTO.DoacaoRecenteDTO;
import com.tcc.desperdicio_alimentos.dto.DashboardImpactoDTO.RankingMercadoDTO;
import com.tcc.desperdicio_alimentos.dto.DashboardImpactoDTO.RankingOngDTO;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import com.tcc.desperdicio_alimentos.repository.PedidoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DoacaoRepository doacaoRepo;
    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;
    private final PedidoRepository pedidoRepo;

    public DashboardController(DoacaoRepository doacaoRepo,
                               ProdutoRepository produtoRepo,
                               UsuarioRepository usuarioRepo,
                               PedidoRepository pedidoRepo) {
        this.doacaoRepo = doacaoRepo;
        this.produtoRepo = produtoRepo;
        this.usuarioRepo = usuarioRepo;
        this.pedidoRepo = pedidoRepo;
    }

    @GetMapping("/impacto")
    public ResponseEntity<DashboardImpactoDTO> getImpacto() {
        DashboardImpactoDTO dto = new DashboardImpactoDTO();

        List<Doacao> todasDoacoes = doacaoRepo.findAll();
        List<Produto> todosProdutos = produtoRepo.findAll();
        List<Usuario> todosUsuarios = usuarioRepo.findAll();

        // Contadores gerais
        dto.totalDoacoes = todasDoacoes.size();
        dto.doacoesPendentes = todasDoacoes.stream().filter(d -> d.getStatus() == StatusDoacao.PENDENTE).count();
        dto.doacoesAceitas = todasDoacoes.stream().filter(d -> d.getStatus() == StatusDoacao.ACEITA).count();
        dto.doacoesRetiradas = todasDoacoes.stream().filter(d -> d.getStatus() == StatusDoacao.RETIRADA).count();
        dto.doacoesRecusadas = todasDoacoes.stream().filter(d -> d.getStatus() == StatusDoacao.RECUSADA).count();
        dto.totalProdutosCadastrados = todosProdutos.size();
        dto.produtosDisponiveis = todosProdutos.stream().filter(p -> Boolean.TRUE.equals(p.getDisponivel())).count();
        dto.totalOngs = todosUsuarios.stream().filter(u -> u.getTipo() == UsuarioTipo.ONG).count();
        dto.totalMercados = todosUsuarios.stream().filter(u -> u.getTipo() == UsuarioTipo.FUNCIONARIO).count();
        dto.totalClientes = todosUsuarios.stream().filter(u -> u.getTipo() == UsuarioTipo.CLIENTE).count();
        dto.totalPedidos = pedidoRepo.count();

        // Total de itens doados (soma quantidade das retiradas concluídas)
        dto.itensDoados = todasDoacoes.stream()
                .filter(d -> d.getStatus() == StatusDoacao.RETIRADA)
                .mapToLong(d -> d.getQuantidade() != null ? d.getQuantidade() : 0)
                .sum();

        // Ranking de ONGs (por retiradas concluídas)
        Map<Long, List<Doacao>> doacoesPorOng = todasDoacoes.stream()
                .filter(d -> d.getOng() != null)
                .collect(Collectors.groupingBy(d -> d.getOng().getId()));

        dto.rankingOngs = doacoesPorOng.entrySet().stream()
                .map(entry -> {
                    List<Doacao> doacoes = entry.getValue();
                    Usuario ong = doacoes.get(0).getOng();
                    long retiradas = doacoes.stream().filter(d -> d.getStatus() == StatusDoacao.RETIRADA).count();
                    return new RankingOngDTO(ong.getId(), ong.getNome(), retiradas, doacoes.size());
                })
                .sorted((a, b) -> Long.compare(b.retiradasConcluidas, a.retiradasConcluidas))
                .limit(10)
                .collect(Collectors.toList());

        // Ranking de Mercados (por total de doações criadas)
        Map<Long, List<Doacao>> doacoesPorMercado = todasDoacoes.stream()
                .filter(d -> d.getCriadoPor() != null)
                .collect(Collectors.groupingBy(d -> d.getCriadoPor().getId()));

        dto.rankingMercados = doacoesPorMercado.entrySet().stream()
                .map(entry -> {
                    List<Doacao> doacoes = entry.getValue();
                    Usuario mercado = doacoes.get(0).getCriadoPor();
                    long itens = doacoes.stream()
                            .mapToLong(d -> d.getQuantidade() != null ? d.getQuantidade() : 0)
                            .sum();
                    return new RankingMercadoDTO(mercado.getId(), mercado.getNome(), doacoes.size(), itens);
                })
                .sorted((a, b) -> Long.compare(b.totalDoacoes, a.totalDoacoes))
                .limit(10)
                .collect(Collectors.toList());

        // Doações recentes (últimas 10)
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dto.doacoesRecentes = todasDoacoes.stream()
                .sorted(Comparator.comparing(Doacao::getDataCriacao, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(d -> new DoacaoRecenteDTO(
                        d.getId(),
                        d.getProduto() != null ? d.getProduto().getNome() : "-",
                        d.getCriadoPor() != null ? d.getCriadoPor().getNome() : "-",
                        d.getOng() != null ? d.getOng().getNome() : "-",
                        d.getStatus().name(),
                        d.getDataCriacao() != null ? d.getDataCriacao().format(fmt) : "-"
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dto);
    }

}
