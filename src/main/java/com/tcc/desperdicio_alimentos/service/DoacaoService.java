package com.tcc.desperdicio_alimentos.service;



import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoacaoService {

    @Autowired
    private DoacaoRepository doacaoRepository;

    public Doacao salvar(Doacao doacao) {
        return doacaoRepository.save(doacao);
    }

    public List<Doacao> listarTodos() {
        return doacaoRepository.findAll();
    }

    public Doacao buscarPorId(Long id) {
        return doacaoRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        doacaoRepository.deleteById(id);
    }
}

