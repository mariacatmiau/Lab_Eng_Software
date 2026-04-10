package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.dto.AtualizarPerfilRequest;
import com.tcc.desperdicio_alimentos.dto.LoginResponseDTO;
import com.tcc.desperdicio_alimentos.dto.UsuarioResumoDTO;
import com.tcc.desperdicio_alimentos.config.JwtService;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.model.UsuarioTipo;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import com.tcc.desperdicio_alimentos.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioController(
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // --- REGISTRO (funcionário, ONG ou cliente)
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuario.getTipo() == null) {
                return ResponseEntity.badRequest().body("Tipo de usuário é obrigatório (FUNCIONARIO, ONG ou CLIENTE)");
            }
            if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Nome é obrigatório");
            }
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("E-mail é obrigatório");
            }
            if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return ResponseEntity.badRequest().body("Formato de e-mail inválido");
            }
            if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Senha é obrigatória");
            }
            if (usuario.getSenha().length() < 6) {
                return ResponseEntity.badRequest().body("Senha deve ter no mínimo 6 caracteres");
            }
            if (usuario.getTelefone() == null || usuario.getTelefone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Telefone é obrigatório");
            }
            if (usuario.getEndereco() == null || usuario.getEndereco().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Endereço é obrigatório para localização");
            }

            usuario.setTipo(UsuarioTipo.valueOf(usuario.getTipo().toString().toUpperCase()));
            Usuario novo = usuarioService.cadastrar(usuario);
            return ResponseEntity.ok(UsuarioResumoDTO.from(novo));

        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-mail já cadastrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar usuário: " + e.getMessage());
        }
    }

    // --- LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String senha = body.get("senha");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        Usuario usuario = usuarioOpt.get();
        boolean senhaValida = passwordEncoder.matches(senha, usuario.getSenha());

        if (!senhaValida) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        String token = jwtService.generateToken(usuario.getId(), usuario.getTipo().name());
        return ResponseEntity.ok(new LoginResponseDTO(token, UsuarioResumoDTO.from(usuario)));
    }

    // --- LISTAR TODOS (debug)
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(usuarioService.listar().stream().map(UsuarioResumoDTO::from).collect(Collectors.toList()));
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> atualizarPerfil(@PathVariable Long id, @RequestBody AtualizarPerfilRequest req, Authentication authentication) {
        try {
            Long usuarioAutenticadoId = Long.parseLong(authentication.getName());
            if (!usuarioAutenticadoId.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não é permitido atualizar outro usuário");
            }
            Usuario atualizado = usuarioService.atualizarPerfil(id, req);
            return ResponseEntity.ok(UsuarioResumoDTO.from(atualizado));
        } catch (Exception e) {
            if (e instanceof org.springframework.web.server.ResponseStatusException rse) {
                return ResponseEntity.status(rse.getStatusCode()).body(rse.getReason());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar perfil: " + e.getMessage());
        }
    }
}
