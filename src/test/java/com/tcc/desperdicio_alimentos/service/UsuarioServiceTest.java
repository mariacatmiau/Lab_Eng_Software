package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.AtualizarPerfilRequest;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    private UsuarioRepository repo;
    private PasswordEncoder passwordEncoder;
    private LocalizacaoService localizacaoService;
    private UsuarioService service;

    @BeforeEach
    void setup() {
        repo = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        localizacaoService = mock(LocalizacaoService.class);
        service = new UsuarioService(repo, passwordEncoder, localizacaoService);
    }

    @Test
    void deveCadastrarUsuario() {
        Usuario u = new Usuario();
        u.setEmail("teste@test.com");
        u.setSenha("123");
        u.setEndereco("Rua Teste, 123 - Centro, Sao Paulo");
        LocalizacaoService.Coordenada coordenada = new LocalizacaoService.Coordenada(-23.55, -46.63);

        when(repo.findByEmail("teste@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("hashed-123");
        when(localizacaoService.buscarCoordenadas("Rua Teste, 123 - Centro, Sao Paulo"))
            .thenReturn(Optional.of(coordenada));
        when(repo.save(u)).thenReturn(u);

        Usuario salvo = service.cadastrar(u);

        assertNotNull(salvo);
        assertEquals(-23.55, salvo.getLatitude());
        assertEquals(-46.63, salvo.getLongitude());
        verify(repo).save(u);
        verify(passwordEncoder).encode("123");
    }

    @Test
    void naoDeveCadastrarEmailDuplicado() {
        Usuario u = new Usuario();
        u.setEmail("teste@test.com");

        when(repo.findByEmail("teste@test.com")).thenReturn(Optional.of(u));

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(u));
    }

    @Test
    void deveAtualizarPerfil() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNome("Antigo");
        u.setEmail("antigo@test.com");

        AtualizarPerfilRequest req = new AtualizarPerfilRequest();
        req.nome = "Novo Nome";
        req.email = "novo@test.com";
        req.telefone = "11999999999";
        req.endereco = "Rua A, 10 - Centro, Sao Paulo";
        LocalizacaoService.Coordenada coordenada = new LocalizacaoService.Coordenada(-23.56, -46.64);

        when(repo.findById(1L)).thenReturn(Optional.of(u));
        when(repo.findByEmail("novo@test.com")).thenReturn(Optional.empty());
        when(localizacaoService.buscarCoordenadas("Rua A, 10 - Centro, Sao Paulo"))
            .thenReturn(Optional.of(coordenada));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Usuario atualizado = service.atualizarPerfil(1L, req);

        assertEquals("Novo Nome", atualizado.getNome());
        assertEquals("novo@test.com", atualizado.getEmail());
        assertEquals("11999999999", atualizado.getTelefone());
        assertEquals("Rua A, 10 - Centro, Sao Paulo", atualizado.getEndereco());
        assertEquals(-23.56, atualizado.getLatitude());
        assertEquals(-46.64, atualizado.getLongitude());
    }

    @Test
    void naoDeveCadastrarQuandoEnderecoNaoPodeSerLocalizado() {
        Usuario u = new Usuario();
        u.setEmail("teste@test.com");
        u.setSenha("123");
        u.setEndereco("Endereco Inventado 99999");

        when(repo.findByEmail("teste@test.com")).thenReturn(Optional.empty());
        when(localizacaoService.buscarCoordenadas("Endereco Inventado 99999")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.cadastrar(u));
        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Não foi possível localizar esse endereço"));
    }
}
