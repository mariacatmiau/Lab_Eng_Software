package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.model.Ong;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.model.StatusDoacao;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.OngRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DoacaoController {

    @Autowired private DoacaoRepository doacaoRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private OngRepository ongRepository;

    // ------- DTO para criação -------
    public record CriarDoacaoDTO(Long produtoId, Long ongId, Integer quantidade) {}

    // ------- Listar todas -------
    @GetMapping("/doacoes")
    public List<Doacao> listarDoacoes() {
        return doacaoRepository.findAll();
    }

    // ------- (Opcional) apenas pendentes -------
    @GetMapping("/doacoes/pendentes")
    public List<Doacao> listarPendentes() {
        return doacaoRepository.findAll()
                .stream().filter(d -> d.getStatus() == StatusDoacao.PENDENTE).toList();
    }

    // ------- Criar doação (funcionário) -------
    @PostMapping("/doacoes")
    public ResponseEntity<?> criarDoacao(@RequestBody CriarDoacaoDTO dto) {
        if (dto.produtoId() == null || dto.ongId() == null) {
            return ResponseEntity.badRequest().body("produtoId e ongId são obrigatórios");
        }

        Optional<Produto> prodOpt = produtoRepository.findById(dto.produtoId());
        Optional<Ong> ongOpt = ongRepository.findById(dto.ongId());

        if (prodOpt.isEmpty() || ongOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Produto ou ONG inválidos.");
        }

        Doacao d = new Doacao();
        d.setProduto(prodOpt.get());
        d.setOng(ongOpt.get());
        d.setQuantidade(dto.quantidade() != null ? dto.quantidade() : 1);
        d.setStatus(StatusDoacao.PENDENTE);
        d.setDataCriacao(LocalDateTime.now());

        Doacao salvo = doacaoRepository.save(d);
        return ResponseEntity.ok(salvo);
    }

    // ------- ONG aceita -------
    @PutMapping("/doacoes/{id}/aceitar")
    public ResponseEntity<?> aceitar(@PathVariable Long id) {
        Optional<Doacao> opt = doacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Doacao d = opt.get();
        d.setStatus(StatusDoacao.ACEITA);
        doacaoRepository.save(d);
        return ResponseEntity.ok("Doação aceita.");
    }

    // ------- ONG recusa -------
    @PutMapping("/doacoes/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable Long id) {
        Optional<Doacao> opt = doacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Doacao d = opt.get();
        d.setStatus(StatusDoacao.RECUSADA);
        doacaoRepository.save(d);
        return ResponseEntity.ok("Doação recusada.");
    }

    // ------- Confirmar retirada (funcionário) -------
    @PutMapping("/retiradas/{id}/confirmar")
    public ResponseEntity<?> confirmarRetirada(@PathVariable Long id) {
        Optional<Doacao> opt = doacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Doacao d = opt.get();
        d.setStatus(StatusDoacao.RETIRADA_CONCLUIDA);
        d.setDataRetirada(LocalDateTime.now());
        doacaoRepository.save(d);
        return ResponseEntity.ok("Retirada confirmada.");
    }

    // ------- Cancelar (funcionário) -------
    @PutMapping("/retiradas/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        Optional<Doacao> opt = doacaoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Doacao d = opt.get();
        d.setStatus(StatusDoacao.CANCELADA);
        doacaoRepository.save(d);
        return ResponseEntity.ok("Doação cancelada.");
    }
}
