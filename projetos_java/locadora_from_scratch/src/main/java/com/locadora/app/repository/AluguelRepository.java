package com.locadora.app.repository;

import com.locadora.app.model.Aluguel;
import com.locadora.app.util.DB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AluguelRepository {

    /** Salvar novo aluguel */
    public void salvar(Aluguel a) throws Exception {
        String sql = """
                INSERT INTO alugueis
                (usuario_id, veiculo_id, data_inicio, data_fim, km_inicio, km_fim, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, a.getUsuarioId());
            ps.setInt(2, a.getVeiculoId());
            ps.setString(3, a.getDataInicio() != null ? a.getDataInicio().toString() : null);
            ps.setString(4, a.getDataFim() != null ? a.getDataFim().toString() : null);
            ps.setObject(5, a.getKmInicio());
            ps.setObject(6, a.getKmFim());
            ps.setString(7, a.getStatus());

            ps.executeUpdate();
        }
    }

    /** Atualizar aluguel existente */
    public void atualizar(Aluguel a) throws Exception {
        String sql = """
                UPDATE alugueis SET
                    usuario_id = ?, veiculo_id = ?, data_inicio = ?, data_fim = ?,
                    km_inicio = ?, km_fim = ?, status = ?
                WHERE id = ?
                """;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, a.getUsuarioId());
            ps.setInt(2, a.getVeiculoId());
            ps.setString(3, a.getDataInicio() != null ? a.getDataInicio().toString() : null);
            ps.setString(4, a.getDataFim() != null ? a.getDataFim().toString() : null);
            ps.setObject(5, a.getKmInicio());
            ps.setObject(6, a.getKmFim());
            ps.setString(7, a.getStatus());
            ps.setInt(8, a.getId());

            ps.executeUpdate();
        }
    }

    /** Remover */
    public void remover(int id) throws Exception {
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM alugueis WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Buscar por ID */
    public Aluguel buscarPorId(int id) throws Exception {
        String sql = "SELECT * FROM alugueis WHERE id = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }
        }
        return null;
    }

    /** Listar todos */
    public List<Aluguel> listar() throws Exception {
        List<Aluguel> lista = new ArrayList<>();

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM alugueis")) {

            while (rs.next()) {
                lista.add(map(rs));
            }
        }

        return lista;
    }

    /** Converter ResultSet em objeto */
    private Aluguel map(ResultSet rs) throws Exception {

        Aluguel a = new Aluguel();
        a.setId(rs.getInt("id"));
        a.setUsuarioId(rs.getInt("usuario_id"));
        a.setVeiculoId(rs.getInt("veiculo_id"));

        String di = rs.getString("data_inicio");
        String df = rs.getString("data_fim");

        a.setDataInicio(di != null ? LocalDate.parse(di) : null);
        a.setDataFim(df != null ? LocalDate.parse(df) : null);

        a.setKmInicio((Integer) rs.getObject("km_inicio"));
        a.setKmFim((Integer) rs.getObject("km_fim"));
        a.setStatus(rs.getString("status"));

        return a;
    }
}
