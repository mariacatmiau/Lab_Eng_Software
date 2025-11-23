package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    private UsuarioRepository repo;
    private UsuarioService service;

    @BeforeEach
    void setup() {
        repo = mock(UsuarioRepository.class);
        service = new UsuarioService(repo);
    }

    @Test
    void deveCadastrarUsuario() {
        Usuario u = new Usuario();
        u.setEmail("teste@test.com");

        when(repo.findByEmail("teste@test.com")).thenReturn(Optional.empty());
        when(repo.save(u)).thenReturn(u);

        Usuario salvo = service.cadastrar(u);

        assertNotNull(salvo);
        verify(repo).save(u);
    }

    @Test
    void naoDeveCadastrarEmailDuplicado() {
        Usuario u = new Usuario();
        u.setEmail("teste@test.com");

        when(repo.findByEmail("teste@test.com")).thenReturn(Optional.of(u));

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(u));
    }
}
