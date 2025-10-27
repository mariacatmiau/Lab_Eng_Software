package com.tcc.desperdicio_alimentos.service;


import com.tcc.desperdicio_alimentos.model.Ong;
import com.tcc.desperdicio_alimentos.repository.OngRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OngService {

    @Autowired
    private OngRepository ongRepository;

    public Ong salvar(Ong ong) {
        return ongRepository.save(ong);
    }

    public List<Ong> listarTodos() {
        return ongRepository.findAll();
    }

    public Ong buscarPorId(Long id) {
        return ongRepository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        ongRepository.deleteById(id);
    }
}

