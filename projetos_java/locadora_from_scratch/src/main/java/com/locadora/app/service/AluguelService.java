package com.locadora.app.service;

import com.locadora.app.model.Aluguel;
import com.locadora.app.model.Veiculo;
import com.locadora.app.repository.AluguelRepository;
import com.locadora.app.repository.VeiculoRepository;

import java.time.LocalDate;
import java.util.List;

public class AluguelService {

    private final AluguelRepository repo = new AluguelRepository();
    private final VeiculoRepository veRepo = new VeiculoRepository();

    /** Criar aluguel (abrir) */
    public Aluguel criarAluguel(Aluguel a) throws Exception {

        if (a.getUsuarioId() == null) throw new IllegalArgumentException("Usuário obrigatório");
        if (a.getVeiculoId() == null) throw new IllegalArgumentException("Veículo obrigatório");

        Veiculo v = veRepo.buscarPorId(a.getVeiculoId());
        if (v == null) throw new IllegalArgumentException("Veículo não encontrado");

        if (!v.isDisponivel()) throw new IllegalStateException("Veículo indisponível");

        if (a.getDataInicio() == null)
            a.setDataInicio(LocalDate.now());

        a.setStatus("ABERTO");

        repo.salvar(a);

        v.setDisponivel(false);
        veRepo.atualizar(v);

        return a;
    }

    /** Listar todos */
    public List<Aluguel> listar() throws Exception {
        return repo.listar();
    }

    /** Remover aluguel */
    public void remover(int id) throws Exception {
        repo.remover(id);
    }

    /** Buscar por ID */
    public Aluguel buscarPorId(int id) throws Exception {
        return repo.buscarPorId(id);
    }

    /** Finalizar aluguel */
    public void finalizarAluguel(Aluguel a) throws Exception {

        if (a.getDataFim() == null)
            throw new IllegalArgumentException("Data fim é obrigatória");

        if (a.getKmFim() == null)
            throw new IllegalArgumentException("KM final é obrigatório");

        if (a.getKmFim() < a.getKmInicio())
            throw new IllegalArgumentException("KM final não pode ser menor que o inicial");

        a.setStatus("FECHADO");

        repo.atualizar(a);

        // Liberar veículo
        Veiculo v = veRepo.buscarPorId(a.getVeiculoId());
        if (v != null) {
            v.setDisponivel(true);
            veRepo.atualizar(v);
        }
    }
}
