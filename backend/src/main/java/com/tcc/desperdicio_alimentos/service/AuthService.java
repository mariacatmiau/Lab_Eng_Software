package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.LoginRequest;
import com.tcc.desperdicio_alimentos.dto.RegisterRequest;
import com.tcc.desperdicio_alimentos.model.*;
import com.tcc.desperdicio_alimentos.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final FuncionarioRepository funcionarioRepo;
    private final OngRepository ongRepo;

    public AuthService(UsuarioRepository u, FuncionarioRepository f, OngRepository o) {
        this.usuarioRepo = u; this.funcionarioRepo = f; this.ongRepo = o;
    }

    public Object register(RegisterRequest req) {
        if (usuarioRepo.existsByEmail(req.email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        Usuario u = new Usuario();
        u.setNome(req.nome);
        u.setEmail(req.email);
        u.setSenha(req.senha); // TODO: usar BCrypt em produção
        u.setTipo(req.tipo);
        usuarioRepo.save(u);

        if (req.tipo == UsuarioTipo.FUNCIONARIO) {
            Funcionario f = new Funcionario();
            f.setUsuario(u);
            return funcionarioRepo.save(f);
        } else {
            Ong ong = new Ong();
            ong.setUsuario(u);
            ong.setCnpj(req.cnpj);
            ong.setEndereco(req.endereco);
            ong.setTelefone(req.telefone);
            return ongRepo.save(ong);
        }
    }

    public Usuario login(LoginRequest req) {
        return usuarioRepo.findByEmail(req.email)
                .filter(u -> u.getSenha().equals(req.senha))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));
    }
}
