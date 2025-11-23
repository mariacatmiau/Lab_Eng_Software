package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoServiceTest {

    private ProdutoService service;
    private ProdutoRepository produtoRepo;
    private UsuarioRepository usuarioRepo;

    @BeforeEach
    void setup() {
        produtoRepo = mock(ProdutoRepository.class);
        usuarioRepo = mock(UsuarioRepository.class);
        service = new ProdutoService(produtoRepo, usuarioRepo);
    }

    @Test
    void deveCriarProduto() {
        CriarProdutoRequest req = new CriarProdutoRequest();
        req.nome = "Arroz";
        req.categoria = "Alimento";
        req.quantidade = 5;
        req.dataValidade = LocalDate.now();
        req.usuarioId = 1L;

        Usuario u = new Usuario();
        u.setId(1L);

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(u));
        when(produtoRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Produto p = service.criar(req);

        assertNotNull(p);
        assertEquals("Arroz", p.getNome());
        assertTrue(p.getDisponivel());
        verify(produtoRepo).save(any());
    }

    @Test
    void deveFalharQuandoUsuarioNaoExiste() {
        CriarProdutoRequest req = new CriarProdutoRequest();
        req.usuarioId = 99L;

        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.criar(req));
    }
}
