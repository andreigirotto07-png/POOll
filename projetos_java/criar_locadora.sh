#!/usr/bin/env bash
set -e

echo ">>> Criando projeto locadora_from_scratch..."

ROOT="locadora_from_scratch"
SRC="$ROOT/src/main/java/com/locadora/app"

mkdir -p "$SRC/util" "$SRC/model" "$SRC/repository" "$SRC/service" "$SRC/ui"

##############################################
# pom.xml
##############################################
cat > "$ROOT/pom.xml" << 'EOF'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.locadora</groupId>
  <artifactId>locadora-app</artifactId>
  <version>1.0-SNAPSHOT</version>

  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.xerial</groupId>
      <artifactId>sqlite-jdbc</artifactId>
      <version>3.41.2.1</version>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.5.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
            <configuration>
              <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                  <mainClass>com.locadora.app.Main</mainClass>
                </transformer>
              </transformers>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
EOF

##############################################
# Main.java
##############################################
cat > "$SRC/Main.java" << 'EOF'
package com.locadora.app;

import com.locadora.app.ui.MainWindow;
import com.locadora.app.util.DB;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            DB.init();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao iniciar banco: " + e.getMessage());
            return;
        }

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
EOF

##############################################
# DB.java
##############################################
cat > "$SRC/util/DB.java" << 'EOF'
package com.locadora.app.util;

import java.nio.file.*;
import java.sql.*;

public class DB {

    private static final String DB_FILE = "data/locadora.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void init() throws Exception {
        Path p = Path.of(DB_FILE);
        if (!Files.exists(p.getParent())) Files.createDirectories(p.getParent());

        boolean exists = Files.exists(p);

        try (Connection c = getConnection(); Statement s = c.createStatement()) {

            if (!exists) {
                s.execute("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        email TEXT,
                        telefone TEXT
                    );
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS veiculos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        placa TEXT UNIQUE NOT NULL,
                        marca TEXT,
                        modelo TEXT,
                        ano INTEGER,
                        cor TEXT,
                        disponivel INTEGER,
                        valor_diaria REAL
                    );
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS alugueis (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        usuario_id INTEGER,
                        veiculo_id INTEGER,
                        data_inicio TEXT,
                        data_fim TEXT,
                        km_inicio INTEGER,
                        km_fim INTEGER,
                        status TEXT,
                        FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                        FOREIGN KEY(veiculo_id) REFERENCES veiculos(id)
                    );
                """);
            }
        }
    }
}
EOF

##############################################
# MODELS
##############################################

### Usuario.java
cat > "$SRC/model/Usuario.java" << 'EOF'
package com.locadora.app.model;

public class Usuario {
    private Integer id;
    private String nome;
    private String email;
    private String telefone;

    public Usuario() {}

    public Usuario(Integer id, String nome, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    @Override
    public String toString() {
        return nome;
    }
}
EOF

### Veiculo.java
cat > "$SRC/model/Veiculo.java" << 'EOF'
package com.locadora.app.model;

public class Veiculo {
    private Integer id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private Boolean disponivel = true;
    private Double valorDiaria = 100.0;

    public Veiculo() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public Boolean isDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }
    public Double getValorDiaria() { return valorDiaria; }

    public void setValorDiaria(Double valorDiaria) { this.valorDiaria = valorDiaria; }

    @Override
    public String toString() {
        return modelo + " (" + placa + ")";
    }
}
EOF

### Aluguel.java
cat > "$SRC/model/Aluguel.java" << 'EOF'
package com.locadora.app.model;

import java.time.LocalDate;

public class Aluguel {
    private Integer id;
    private Integer usuarioId;
    private Integer veiculoId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer kmInicio;
    private Integer kmFim;
    private String status;

    public Aluguel() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Integer veiculoId) { this.veiculoId = veiculoId; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Integer getKmInicio() { return kmInicio; }
    public void setKmInicio(Integer kmInicio) { this.kmInicio = kmInicio; }
    public Integer getKmFim() { return kmFim; }
    public void setKmFim(Integer kmFim) { this.kmFim = kmFim; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
EOF

##############################################
# REPOSITORIES
##############################################

### UsuarioRepository.java
cat > "$SRC/repository/UsuarioRepository.java" << 'EOF'
package com.locadora.app.repository;

import com.locadora.app.model.Usuario;
import com.locadora.app.util.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    public Usuario salvar(Usuario u) throws Exception {
        String sql = "INSERT INTO usuarios(nome,email,telefone) VALUES(?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTelefone());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        }
        return u;
    }

    public void atualizar(Usuario u) throws Exception {
        String sql = "UPDATE usuarios SET nome=?,email=?,telefone=? WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTelefone());
            ps.setInt(4, u.getId());
            ps.executeUpdate();
        }
    }

    public void remover(int id) throws Exception {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Usuario> listar() throws Exception {
        List<Usuario> list = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("telefone")
                );
                list.add(u);
            }
        }
        return list;
    }
}
EOF

### VeiculoRepository.java
cat > "$SRC/repository/VeiculoRepository.java" << 'EOF'
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
}
EOF

### AluguelRepository.java
cat > "$SRC/repository/AluguelRepository.java" << 'EOF'
package com.locadora.app.repository;

import com.locadora.app.model.Aluguel;
import com.locadora.app.util.DB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AluguelRepository {

    public Aluguel salvar(Aluguel a) throws Exception {
        String sql = "INSERT INTO alugueis(usuario_id,veiculo_id,data_inicio,data_fim,km_inicio,km_fim,status) VALUES(?,?,?,?,?,?,?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (a.getUsuarioId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, a.getUsuarioId());
            if (a.getVeiculoId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, a.getVeiculoId());

            ps.setString(3, a.getDataInicio() != null ? a.getDataInicio().toString() : null);
            ps.setString(4, a.getDataFim() != null ? a.getDataFim().toString() : null);

            if (a.getKmInicio() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, a.getKmInicio());
            if (a.getKmFim() == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, a.getKmFim());

            ps.setString(7, a.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
        }

        return a;
    }

    public List<Aluguel> listar() throws Exception {
        List<Aluguel> list = new ArrayList<>();
        String sql = "SELECT * FROM alugueis ORDER BY id DESC";

        try (Connection c = DB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Aluguel a = new Aluguel();
                a.setId(rs.getInt("id"));
                a.setUsuarioId((Integer) rs.getObject("usuario_id"));
                a.setVeiculoId((Integer) rs.getObject("veiculo_id"));

                String di = rs.getString("data_inicio");
                a.setDataInicio(di != null ? LocalDate.parse(di) : null);

                String df = rs.getString("data_fim");
                a.setDataFim(df != null ? LocalDate.parse(df) : null);

                a.setKmInicio((Integer) rs.getObject("km_inicio"));
                a.setKmFim((Integer) rs.getObject("km_fim"));
                a.setStatus(rs.getString("status"));

                list.add(a);
            }
        }

        return list;
    }

    public void remover(int id) throws Exception {
        String sql = "DELETE FROM alugueis WHERE id=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
EOF

##############################################
# SERVICES
##############################################

### UsuarioService.java
cat > "$SRC/service/UsuarioService.java" << 'EOF'
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
EOF

### VeiculoService.java
cat > "$SRC/service/VeiculoService.java" << 'EOF'
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
EOF

### AluguelService.java
cat > "$SRC/service/AluguelService.java" << 'EOF'
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

    public Aluguel criarAluguel(Aluguel a) throws Exception {

        if (a.getUsuarioId() == null) throw new IllegalArgumentException("Usuário obrigatório");
        if (a.getVeiculoId() == null) throw new IllegalArgumentException("Veículo obrigatório");

        Veiculo v = veRepo.listar()
                .stream()
                .filter(x -> x.getId().equals(a.getVeiculoId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        if (!v.isDisponivel()) throw new IllegalStateException("Veículo indisponível");

        if (a.getDataInicio() == null) a.setDataInicio(LocalDate.now());
        a.setStatus("ABERTO");

        repo.salvar(a);

        v.setDisponivel(false);
        veRepo.atualizar(v);

        return a;
    }

    public List<Aluguel> listar() throws Exception {
        return repo.listar();
    }

    public void remover(int id) throws Exception {
        repo.remover(id);
    }
}
EOF

##############################################
# UI FILES (mais curtos, para não ultrapassar limite)
##############################################

### MainWindow.java
cat > "$SRC/ui/MainWindow.java" << 'EOF'
package com.locadora.app.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Locadora de Veículos");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton btnUsuarios = new JButton("Usuários");
        JButton btnVeiculos = new JButton("Veículos");
        JButton btnAlugueis = new JButton("Aluguéis");

        btnUsuarios.addActionListener(e -> new UsuariosListDialog(this).setVisible(true));
        btnVeiculos.addActionListener(e -> new VeiculosListDialog(this).setVisible(true));
        btnAlugueis.addActionListener(e -> new AlugueisListDialog(this).setVisible(true));

        p.add(btnUsuarios);
        p.add(btnVeiculos);
        p.add(btnAlugueis);

        add(p);
    }
}
EOF

### UsuariosListDialog.java
cat > "$SRC/ui/UsuariosListDialog.java" << 'EOF'
package com.locadora.app.ui;

import com.locadora.app.model.Usuario;
import com.locadora.app.service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuariosListDialog extends JDialog {

    private JTable tabela;
    private final UsuarioService service = new UsuarioService();

    public UsuariosListDialog(Frame parent) {
        super(parent, true);

        setTitle("Usuários");
        setSize(600, 400);
        setLocationRelativeTo(parent);

        tabela = new JTable();

        JButton btnAdd = new JButton("Adicionar");
        btnAdd.addActionListener(e ->
                new UsuarioFormDialog(this, null, this::carregar).setVisible(true));

        JButton btnEdit = new JButton("Editar");
        btnEdit.addActionListener(e -> editar());

        JButton btnDel = new JButton("Excluir");
        btnDel.addActionListener(e -> excluir());

        JPanel botoes = new JPanel();
        botoes.add(btnAdd);
        botoes.add(btnEdit);
        botoes.add(btnDel);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        carregar();
    }

    private void carregar() {
        try {
            List<Usuario> list = service.listar();

            DefaultTableModel m = new DefaultTableModel(
                    new Object[]{"ID","Nome","Email","Telefone"}, 0
            );

            for (Usuario u : list) {
                m.addRow(new Object[]{u.getId(), u.getNome(), u.getEmail(), u.getTelefone()});
            }

            tabela.setModel(m);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void editar() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário");
            return;
        }

        Usuario u = new Usuario(
                (Integer) tabela.getValueAt(r,0),
                (String) tabela.getValueAt(r,1),
                (String) tabela.getValueAt(r,2),
                (String) tabela.getValueAt(r,3)
        );

        new UsuarioFormDialog(this, u, this::carregar).setVisible(true);
    }

    private void excluir() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário");
            return;
        }

        try {
            int id = (int) tabela.getValueAt(r, 0);
            service.remover(id);
            carregar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
EOF

### UsuarioFormDialog.java
cat > "$SRC/ui/UsuarioFormDialog.java" << 'EOF'
package com.locadora.app.ui;

import com.locadora.app.model.Usuario;
import com.locadora.app.service.UsuarioService;

import javax.swing.*;
import java.awt.*;

public class UsuarioFormDialog extends JDialog {

    private Usuario usuario;
    private final Runnable onSave;
    private final UsuarioService service = new UsuarioService();

    private JTextField txtNome, txtEmail, txtTelefone;

    public UsuarioFormDialog(Frame parent, Usuario usuario, Runnable onSave) {
        super(parent, true);

        this.usuario = usuario;
        this.onSave = onSave;

        setTitle(usuario == null ? "Novo Usuário" : "Editar Usuário");
        setSize(350, 220);
        setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridLayout(4,2,8,8));

        txtNome = new JTextField(usuario != null ? usuario.getNome() : "");
        txtEmail = new JTextField(usuario != null ? usuario.getEmail() : "");
        txtTelefone = new JTextField(usuario != null ? usuario.getTelefone() : "");

        p.add(new JLabel("Nome:")); p.add(txtNome);
        p.add(new JLabel("Email:")); p.add(txtEmail);
        p.add(new JLabel("Telefone:")); p.add(txtTelefone);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());

        p.add(new JPanel());
        p.add(btnSalvar);

        add(p);
    }

    private void salvar() {
        try {

            if (usuario == null)
                usuario = new Usuario();

            usuario.setNome(txtNome.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setTelefone(txtTelefone.getText().trim());

            service.salvar(usuario);

            onSave.run();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
EOF

### VeiculosListDialog.java
cat > "$SRC/ui/VeiculosListDialog.java" << 'EOF'
package com.locadora.app.ui;

import com.locadora.app.model.Veiculo;
import com.locadora.app.service.VeiculoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VeiculosListDialog extends JDialog {

    private JTable tabela;
    private final VeiculoService service = new VeiculoService();

    public VeiculosListDialog(Frame parent) {
        super(parent, true);

        setTitle("Veículos");
        setSize(700, 450);
        setLocationRelativeTo(parent);

        tabela = new JTable();

        JButton btnAdd = new JButton("Novo");
        btnAdd.addActionListener(e -> abrirForm(null));

        JButton btnEdit = new JButton("Editar");
        btnEdit.addActionListener(e -> editar());

        JButton btnDel = new JButton("Excluir");
        btnDel.addActionListener(e -> excluir());

        JPanel botoes = new JPanel();
        botoes.add(btnAdd);
        botoes.add(btnEdit);
        botoes.add(btnDel);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        carregar();
    }

    private void carregar() {
        try {
            List<Veiculo> list = service.listar();

            DefaultTableModel m = new DefaultTableModel(
                new Object[]{"ID","Placa","Marca","Modelo","Ano","Cor","Disponível","Valor/Dia"}, 0
            );

            for (Veiculo v : list) {
                m.addRow(new Object[]{
                        v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
                        v.getAno(), v.getCor(), v.isDisponivel(), v.getValorDiaria()
                });
            }

            tabela.setModel(m);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void abrirForm(Veiculo v) {
        new VeiculoFormDialog(this, v, this::carregar).setVisible(true);
    }

    private void editar() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo");
            return;
        }

        Veiculo v = new Veiculo();
        v.setId((Integer) tabela.getValueAt(r,0));
        v.setPlaca((String) tabela.getValueAt(r,1));
        v.setMarca((String) tabela.getValueAt(r,2));
        v.setModelo((String) tabela.getValueAt(r,3));
        v.setAno((Integer) tabela.getValueAt(r,4));
        v.setCor((String) tabela.getValueAt(r,5));
        v.setDisponivel((Boolean) tabela.getValueAt(r,6));
        v.setValorDiaria((Double) tabela.getValueAt(r,7));

        abrirForm(v);
    }

    private void excluir() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo");
            return;
        }

        try {
            int id = (int) tabela.getValueAt(r,0);
            service.remover(id);
            carregar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
EOF

### VeiculoFormDialog.java
cat > "$SRC/ui/VeiculoFormDialog.java" << 'EOF'
package com.locadora.app.ui;

import com.locadora.app.model.Veiculo;
import com.locadora.app.service.VeiculoService;

import javax.swing.*;
import java.awt.*;

public class VeiculoFormDialog extends JDialog {

    private Veiculo veiculo;
    private final Runnable onSave;
    private final VeiculoService service = new VeiculoService();

    private JTextField txtPlaca, txtMarca, txtModelo, txtAno, txtCor, txtValor;

    public VeiculoFormDialog(Frame parent, Veiculo veiculo, Runnable onSave) {
        super(parent, true);

        this.veiculo = veiculo;
        this.onSave = onSave;

        setTitle(veiculo == null ? "Novo Veículo" : "Editar Veículo");
        setSize(400, 320);
        setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridLayout(7,2,8,8));

        txtPlaca = new JTextField(veiculo != null ? veiculo.getPlaca() : "");
        txtMarca = new JTextField(veiculo != null ? veiculo.getMarca() : "");
        txtModelo = new JTextField(veiculo != null ? veiculo.getModelo() : "");
        txtAno = new JTextField(veiculo != null && veiculo.getAno() != null ? veiculo.getAno().toString() : "");
        txtCor = new JTextField(veiculo != null ? veiculo.getCor() : "");
        txtValor = new JTextField(veiculo != null ? veiculo.getValorDiaria().toString() : "");

        p.add(new JLabel("Placa:")); p.add(txtPlaca);
        p.add(new JLabel("Marca:")); p.add(txtMarca);
        p.add(new JLabel("Modelo:")); p.add(txtModelo);
        p.add(new JLabel("Ano:")); p.add(txtAno);
        p.add(new JLabel("Cor:")); p.add(txtCor);
        p.add(new JLabel("Valor diária:")); p.add(txtValor);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());

        p.add(new JPanel());
        p.add(btnSalvar);

        add(p);
    }

    private void salvar() {
        try {

            if (veiculo == null) veiculo = new Veiculo();

            veiculo.setPlaca(txtPlaca.getText().trim().toUpperCase());
            veiculo.setMarca(txtMarca.getText().trim());
            veiculo.setModelo(txtModelo.getText().trim());
            veiculo.setAno(txtAno.getText().isBlank() ? null : Integer.parseInt(txtAno.getText().trim()));
            veiculo.setCor(txtCor.getText().trim());
            veiculo.setValorDiaria(Double.parseDouble(txtValor.getText().trim()));

            service.salvar(veiculo);

            onSave.run();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
EOF

### AlugueisListDialog.java
cat > "$SRC/ui/AlugueisListDialog.java" << 'EOF'
package com.locadora.app.ui;

import com.locadora.app.model.Aluguel;
import com.locadora.app.service.AluguelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlugueisListDialog extends JDialog {

    private JTable tabela;
    private final AluguelService service = new AluguelService();

    public AlugueisListDialog(Frame parent) {
        super(parent, true);

        setTitle("Aluguéis");
        setSize(750, 450);
        setLocationRelativeTo(parent);

        tabela = new JTable();

        JButton btnAdd = new JButton("Novo Aluguel");
        JButton btnDel = new JButton("Excluir");

        btnAdd.addActionListener(e -> new AluguelFormDialog(this, this::carregar).setVisible(true));
        btnDel.addActionListener(e -> excluir());

        JPanel botoes = new JPanel();
        botoes.add(btnAdd);
        botoes.add(btnDel);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        carregar();
    }

    private void carregar() {
        try {
            List<Aluguel> list = service.listar();

            DefaultTableModel m = new DefaultTableModel(
                new Object[]{"ID","Usuário","Veículo","Início","Fim","Km Início","Km Fim","Status"}, 0
            );

            for (Aluguel a : list) {
                m.addRow(new Object[]{
                    a.getId(),
                    a.getUsuarioId(),
                    a.getVeiculoId(),
                    a.getDataInicio(),
                    a.getDataFim(),
                    a.getKmInicio(),
                    a.getKmFim(),
                    a.getStatus()
                });
            }

            tabela.setModel(m);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void excluir() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluguel");
            return;
        }

        try {
            int id = (int) tabela.getValueAt(r, 0);
            service.remover(id);
            carregar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
EOF

### AluguelFormDialog.java
cat > "$SRC/ui/AluguelFormDialog.java" << 'EOF'
package com.locadora.app.ui;

import com.locadora.app.model.Aluguel;
import com.locadora.app.model.Usuario;
import com.locadora.app.model.Veiculo;
import com.locadora.app.service.AluguelService;
import com.locadora.app.service.UsuarioService;
import com.locadora.app.service.VeiculoService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AluguelFormDialog extends JDialog {

    private final AluguelService aluguelService = new AluguelService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final VeiculoService veiculoService = new VeiculoService();

    private JComboBox<Usuario> cbUsuario;
    private JComboBox<Veiculo> cbVeiculo;
    private JTextField txtKm;

    private final Runnable onSave;

    public AluguelFormDialog(Frame parent, Runnable onSave) {
        super(parent, true);
        this.onSave = onSave;

        setTitle("Novo Aluguel");
        setSize(450, 240);
        setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));

        try {
            List<Usuario> usuarios = usuarioService.listar();
            List<Veiculo> veiculos = veiculoService.listar();
            cbUsuario = new JComboBox<>(usuarios.toArray(new Usuario[0]));
            cbVeiculo = new JComboBox<>(veiculos.toArray(new Veiculo[0]));
        } catch (Exception e) {
            cbUsuario = new JComboBox<>();
            cbVeiculo = new JComboBox<>();
        }

        txtKm = new JTextField("0");

        p.add(new JLabel("Usuário:")); p.add(cbUsuario);
        p.add(new JLabel("Veículo:")); p.add(cbVeiculo);
        p.add(new JLabel("Km inicial:")); p.add(txtKm);

        JButton btn = new JButton("Salvar");
        btn.addActionListener(e -> salvar());

        p.add(new JPanel());
        p.add(btn);

        add(p);
    }

    private void salvar() {
        try {
            Usuario u = (Usuario) cbUsuario.getSelectedItem();
            Veiculo v = (Veiculo) cbVeiculo.getSelectedItem();

            if (u == null || v == null)
                throw new IllegalArgumentException("Selecione usuário e veículo");

            Aluguel a = new Aluguel();
            a.setUsuarioId(u.getId());
            a.setVeiculoId(v.getId());
            a.setDataInicio(LocalDate.now());
            a.setKmInicio(Integer.parseInt(txtKm.getText().trim()));
            a.setStatus("ABERTO");

            aluguelService.criarAluguel(a);

            onSave.run();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
EOF

echo ">>> Projeto criado com sucesso!"
echo "Para compilar:"
echo "  cd locadora_from_scratch"
echo "  mvn clean package"
echo "Para executar:"
echo "  java -jar target/locadora-app-1.0-SNAPSHOT.jar"

