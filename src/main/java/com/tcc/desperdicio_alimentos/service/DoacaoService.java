package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.model.StatusDoacao;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DoacaoService {

    @Autowired
    private DoacaoRepository doacaoRepository;

    // 🔵 Criar nova doação (usado pelo funcionário)
    public Doacao criarDoacao(Doacao doacao) {
        doacao.setStatus(StatusDoacao.PENDENTE);
        doacao.setDataCriacao(LocalDateTime.now());
        return doacaoRepository.save(doacao);
    }

    // 🟢 Buscar todas as doações
    public List<Doacao> listarTodas() {
        return doacaoRepository.findAll();
    }

    // 🟣 Buscar todas as retiradas (mesmo endpoint para ONG e funcionário)
    public List<Doacao> listarRetiradas() {
        return doacaoRepository.findAll();
    }

    // 🟢 Buscar apenas doações por status (ex: PENDENTE, CONFIRMADA, etc.)
    public List<Doacao> listarPorStatus(StatusDoacao status) {
        return doacaoRepository.findByStatus(status);
    }

    // ✅ Confirmar retirada (ONG ou funcionário)
    public Optional<Doacao> confirmarRetirada(Long id) {
        Optional<Doacao> opt = doacaoRepository.findById(id);
        if (opt.isPresent()) {
            Doacao doacao = opt.get();
            doacao.setStatus(StatusDoacao.RETIRADA_CONCLUIDA);
            doacao.setDataRetirada(LocalDateTime.now());
            doacaoRepository.save(doacao);
            return Optional.of(doacao);
        }
        return Optional.empty();
    }

    // ❌ Cancelar retirada (somente funcionário)
    public Optional<Doacao> cancelarRetirada(Long id) {
        Optional<Doacao> opt = doacaoRepository.findById(id);
        if (opt.isPresent()) {
            Doacao doacao = opt.get();
            doacao.setStatus(StatusDoacao.CANCELADA);
            doacaoRepository.save(doacao);
            return Optional.of(doacao);
        }
        return Optional.empty();
    }

    // 🟠 Atualizar status genérico (caso queira usar depois)
    public Optional<Doacao> atualizarStatus(Long id, StatusDoacao novoStatus) {
        Optional<Doacao> opt = doacaoRepository.findById(id);
        if (opt.isPresent()) {
            Doacao doacao = opt.get();
            doacao.setStatus(novoStatus);
            doacaoRepository.save(doacao);
            return Optional.of(doacao);
        }
        return Optional.empty();
    }
}
