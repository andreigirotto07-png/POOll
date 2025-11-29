package com.locadora.app.service;

import com.locadora.app.model.Usuario;
import com.locadora.app.repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {

    private final UsuarioRepository repo = new UsuarioRepository();

    public Usuario salvar(Usuario u) throws Exception {
        if (u.getNome() == null || u.getNome().isBlank())
            throw new IllegalArgumentException("Nome obrigatório");

        if (u.getId() == null) return repo.salvar(u);
        repo.atualizar(u);
        return u;
    }

    public List<Usuario> listar() throws Exception {
        return repo.listar();
    }

    public void remover(int id) throws Exception {
        repo.remover(id);
    }
}
