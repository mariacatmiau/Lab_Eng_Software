package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.LoginRequest;
import com.tcc.desperdicio_alimentos.dto.RegisterRequest;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final FuncionarioRepository funcionarioRepo;
    private final OngRepository ongRepo;

    public AuthService(UsuarioRepository usuarioRepo, FuncionarioRepository funcionarioRepo, OngRepository ongRepo) {
        this.usuarioRepo = usuarioRepo;
        this.funcionarioRepo = funcionarioRepo;
        this.ongRepo = ongRepo;
    }

    public Map<String, Object> register(RegisterRequest req) {
        if (usuarioRepo.existsByEmail(req.email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        Usuario u = new Usuario();
        u.setNome(req.nome);
        u.setEmail(req.email);
        u.setSenha(req.senha);
        u.setTipo(req.tipo);
        usuarioRepo.save(u);

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", u);

        if (req.tipo == UsuarioTipo.FUNCIONARIO) {
            Funcionario f = new Funcionario();
            f.setUsuario(u);
            funcionarioRepo.save(f);
            response.put("funcionarioId", f.getId());
        } else {
            Ong o = new Ong();
            o.setUsuario(u);
            o.setCnpj(req.cnpj);
            o.setEndereco(req.endereco);
            o.setTelefone(req.telefone);
            ongRepo.save(o);
            response.put("ongId", o.getId());
        }

        response.put("mensagem", "Cadastro realizado com sucesso!");
        return response;
    }

    public Map<String, Object> login(LoginRequest req) {
        Usuario user = usuarioRepo.findByEmail(req.email)
                .filter(u -> u.getSenha().equals(req.senha))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        Map<String, Object> response = new HashMap<>();
        response.put("usuario", user);

        // adiciona id do funcionário ou ONG se existir
        if (user.getTipo() == UsuarioTipo.FUNCIONARIO) {
            funcionarioRepo.findByUsuarioId(user.getId())
                    .ifPresent(f -> response.put("funcionarioId", f.getId()));
        } else if (user.getTipo() == UsuarioTipo.ONG) {
            ongRepo.findByUsuarioId(user.getId())
                    .ifPresent(o -> response.put("ongId", o.getId()));
        }

        return response;
    }
}
