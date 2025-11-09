package com.tcc.desperdicio_alimentos.service;

import com.tcc.desperdicio_alimentos.dto.CriarProdutoRequest;
import com.tcc.desperdicio_alimentos.model.Produto;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.repository.ProdutoRepository;
import com.tcc.desperdicio_alimentos.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepo;
    private final UsuarioRepository usuarioRepo;

    public ProdutoService(ProdutoRepository produtoRepo, UsuarioRepository usuarioRepo) {
        this.produtoRepo = produtoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Criar produto
    public Produto criar(CriarProdutoRequest req) {
        Usuario u = usuarioRepo.findById(req.usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Produto p = new Produto();
        p.setNome(req.nome);
        p.setCategoria(req.categoria);
        p.setDataValidade(req.dataValidade);
        p.setQuantidade(req.quantidade);
        p.setDisponivel(true);
        p.setCriadoPor(u);

        return produtoRepo.save(p);
    }

    // Listar apenas produtos disponíveis
    public List<Produto> listarDisponiveis() {
        return produtoRepo.findByDisponivelTrue();
    }

    // Listar produtos criados por um usuário específico
    public List<Produto> listarPorUsuario(Long usuarioId) {
        Usuario u = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return produtoRepo.findByCriadoPor(u);
    }

    // Listar todos os produtos (para debug/admin)
    public List<Produto> listarTodos() {
        return produtoRepo.findAll();
    }

    // Marcar produto como indisponível
    public Produto marcarIndisponivel(Long id) {
        Produto p = produtoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        p.setDisponivel(false);
        return produtoRepo.save(p);
    }
}
