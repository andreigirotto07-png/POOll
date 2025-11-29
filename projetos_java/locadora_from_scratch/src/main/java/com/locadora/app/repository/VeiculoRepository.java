package com.locadora.app.repository;

import com.locadora.app.model.Veiculo;
import com.locadora.app.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoRepository {

    public Veiculo salvar(Veiculo v) throws Exception {
        String sql = "INSERT INTO veiculos(placa,marca,modelo,ano,cor,disponivel,valor_diaria) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            if (v.getAno() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, v.getAno());
            ps.setString(5, v.getCor());
            ps.setInt(6, v.isDisponivel() ? 1 : 0);
            ps.setDouble(7, v.getValorDiaria());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) v.setId(rs.getInt(1));
            }
        }
        return v;
    }

    public void atualizar(Veiculo v) throws Exception {
        String sql = "UPDATE veiculos SET placa=?,marca=?,modelo=?,ano=?,cor=?,disponivel=?,valor_diaria=? WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            if (v.getAno() == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, v.getAno());
            ps.setString(5, v.getCor());
            ps.setInt(6, v.isDisponivel() ? 1 : 0);
            ps.setDouble(7, v.getValorDiaria());
            ps.setInt(8, v.getId());
            ps.executeUpdate();
        }
    }

    public void remover(int id) throws Exception {
        String sql = "DELETE FROM veiculos WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Veiculo> listar() throws Exception {
        List<Veiculo> list = new ArrayList<>();
        String sql = "SELECT * FROM veiculos ORDER BY modelo";

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Veiculo v = new Veiculo();
                v.setId(rs.getInt("id"));
                v.setPlaca(rs.getString("placa"));
                v.setMarca(rs.getString("marca"));
                v.setModelo(rs.getString("modelo"));

                int ano = rs.getInt("ano");
                v.setAno(rs.wasNull() ? null : ano);

                v.setCor(rs.getString("cor"));
                v.setDisponivel(rs.getInt("disponivel") == 1);
                v.setValorDiaria(rs.getDouble("valor_diaria"));

                list.add(v);
            }
        }
        return list;
    }

    // 🔍 ➤ MÉTODO QUE ESTAVA FALTANDO
    public Veiculo buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM veiculos WHERE id = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Veiculo v = new Veiculo();
                    v.setId(rs.getInt("id"));
                    v.setPlaca(rs.getString("placa"));
                    v.setMarca(rs.getString("marca"));
                    v.setModelo(rs.getString("modelo"));

                    int ano = rs.getInt("ano");
                    v.setAno(rs.wasNull() ? null : ano);

                    v.setCor(rs.getString("cor"));
                    v.setDisponivel(rs.getInt("disponivel") == 1);
                    v.setValorDiaria(rs.getDouble("valor_diaria"));

                    return v;
                }
            }
        }

        return null;
    }
}
