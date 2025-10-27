package com.tcc.desperdicio_alimentos.controller;


import com.tcc.desperdicio_alimentos.model.Doacao;
import com.tcc.desperdicio_alimentos.service.DoacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doacoes")
public class DoacaoController {

    @Autowired
    private DoacaoService doacaoService;

    @PostMapping
    public Doacao cadastrarDoacao(@RequestBody Doacao doacao) {
        return doacaoService.salvar(doacao);
    }

    @GetMapping
    public List<Doacao> listarDoacoes() {
        return doacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Doacao buscarDoacao(@PathVariable Long id) {
        return doacaoService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarDoacao(@PathVariable Long id) {
        doacaoService.deletar(id);
    }
}
