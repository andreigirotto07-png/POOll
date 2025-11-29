package com.locadora.app.service;

import com.locadora.app.model.Veiculo;
import com.locadora.app.repository.VeiculoRepository;

import java.util.List;

public class VeiculoService {

    private final VeiculoRepository repo = new VeiculoRepository();

    public Veiculo salvar(Veiculo v) throws Exception {
        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new IllegalArgumentException("Placa obrigatória");

        if (v.getId() == null) return repo.salvar(v);
        repo.atualizar(v);
        return v;
    }

    public List<Veiculo> listar() throws Exception {
        return repo.listar();
    }

    public void remover(int id) throws Exception {
        repo.remover(id);
    }
}
