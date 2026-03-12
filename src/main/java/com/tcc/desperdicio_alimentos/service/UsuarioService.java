package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.AtualizarPerfilRequest;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalizacaoService localizacaoService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          LocalizacaoService localizacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.localizacaoService = localizacaoService;
    }

    // Cadastrar novo usuário
    public Usuario cadastrar(Usuario usuario) {
        // Evita duplicação de e-mails
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        LocalizacaoService.Coordenada coordenada = validarEnderecoLocalizavel(usuario.getEndereco());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setEndereco(usuario.getEndereco().trim());
        usuario.setLatitude(coordenada.latitude());
        usuario.setLongitude(coordenada.longitude());
        return usuarioRepository.save(usuario);
    }

    // Listar todos
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // Buscar por e-mail
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario atualizarPerfil(Long usuarioId, AtualizarPerfilRequest req) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (req.nome == null || req.nome.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }

        if (req.email == null || req.email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail é obrigatório");
        }

        if (req.telefone == null || req.telefone.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone é obrigatório");
        }

        if (req.endereco == null || req.endereco.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Endereço é obrigatório para localização");
        }

        String novoEmail = req.email.trim();
        if (!novoEmail.equalsIgnoreCase(usuario.getEmail())) {
            Optional<Usuario> existente = usuarioRepository.findByEmail(novoEmail);
            if (existente.isPresent() && !existente.get().getId().equals(usuarioId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
            }
        }

        LocalizacaoService.Coordenada coordenada = validarEnderecoLocalizavel(req.endereco);

        usuario.setNome(req.nome.trim());
        usuario.setEmail(novoEmail);
        usuario.setTelefone(req.telefone.trim());
        usuario.setEndereco(req.endereco.trim());
        usuario.setLatitude(coordenada.latitude());
        usuario.setLongitude(coordenada.longitude());

        return usuarioRepository.save(usuario);
    }

    private LocalizacaoService.Coordenada validarEnderecoLocalizavel(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Endereço é obrigatório para localização");
        }

        String enderecoNormalizado = endereco.trim();
        Optional<LocalizacaoService.Coordenada> coordenada = localizacaoService.buscarCoordenadas(enderecoNormalizado);
        if (coordenada.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não foi possível localizar esse endereço. Informe rua, número, bairro e cidade válidos."
            );
        }

        return coordenada.get();
    }
}
