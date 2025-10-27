package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.model.Ong;
import com.tcc.desperdicio_alimentos.service.OngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ongs")
public class OngController {

    @Autowired
    private OngService ongService;

    @PostMapping
    public Ong cadastrarOng(@RequestBody Ong ong) {
        return ongService.salvar(ong);
    }

    @GetMapping
    public List<Ong> listarOngs() {
        return ongService.listarTodos();
    }

    @GetMapping("/{id}")
    public Ong buscarOng(@PathVariable Long id) {
        return ongService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarOng(@PathVariable Long id) {
        ongService.deletar(id);
    }
}
