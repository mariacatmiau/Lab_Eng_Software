package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarDoacaoRequest;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.DoacaoRepository;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DoacaoServiceTest {

    private DoacaoService service;
    private DoacaoRepository doacaoRepo;
    private ProdutoRepository produtoRepo;
    private UsuarioRepository usuarioRepo;

    @BeforeEach
    void setup() {
        doacaoRepo = mock(DoacaoRepository.class);
        produtoRepo = mock(ProdutoRepository.class);
        usuarioRepo = mock(UsuarioRepository.class);
        service = new DoacaoService(doacaoRepo, produtoRepo, usuarioRepo);
    }

    @Test
    void deveCriarDoacaoComSucesso() {
        CriarDoacaoRequest req = new CriarDoacaoRequest();
        req.produtoId = 1L;
        req.ongId = 2L;
        req.criadoPorId = 3L;
        req.quantidade = 5;

        Produto p = new Produto();
        p.setId(1L);
        p.setQuantidade(10);
        p.setDisponivel(true);

        Usuario ong = new Usuario();
        ong.setId(2L);

        Usuario funcionario = new Usuario();
        funcionario.setId(3L);

        when(produtoRepo.findById(1L)).thenReturn(Optional.of(p));
        when(usuarioRepo.findById(2L)).thenReturn(Optional.of(ong));
        when(usuarioRepo.findById(3L)).thenReturn(Optional.of(funcionario));
        when(doacaoRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Doacao result = service.criar(req);

        assertNotNull(result);
        assertEquals(StatusDoacao.PENDENTE, result.getStatus());
        assertFalse(p.getDisponivel());
        verify(doacaoRepo, times(1)).save(any());
    }

    @Test
    void deveFalharQuandoProdutoNaoExiste() {
        CriarDoacaoRequest req = new CriarDoacaoRequest();
        req.produtoId = 1L;
        req.ongId = 2L;
        req.criadoPorId = 3L;

        when(produtoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.criar(req));
    }

    @Test
    void deveAceitarDoacao() {
        Doacao d = new Doacao();
        d.setId(1L);
        d.setStatus(StatusDoacao.PENDENTE);

        when(doacaoRepo.findById(1L)).thenReturn(Optional.of(d));
        when(doacaoRepo.save(d)).thenReturn(d);

        Doacao result = service.aceitar(1L);

        assertEquals(StatusDoacao.ACEITA, result.getStatus());
        verify(doacaoRepo).save(d);
    }

    @Test
    void deveRecusarDoacao() {
        Doacao d = new Doacao();
        d.setId(1L);
        d.setStatus(StatusDoacao.PENDENTE);

        when(doacaoRepo.findById(1L)).thenReturn(Optional.of(d));
        when(doacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Doacao result = service.recusar(1L);

        assertNotNull(result);
        assertEquals(StatusDoacao.RECUSADA, result.getStatus());
    }


    @Test
    void deveConfirmarRetirada() {
        Doacao d = new Doacao();
        d.setId(1L);
        d.setStatus(StatusDoacao.PENDENTE);

        when(doacaoRepo.findById(1L)).thenReturn(Optional.of(d));
        when(doacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Doacao result = service.confirmarRetirada(1L);

        assertNotNull(result);
        assertEquals(StatusDoacao.RETIRADA, result.getStatus());
    }
}
