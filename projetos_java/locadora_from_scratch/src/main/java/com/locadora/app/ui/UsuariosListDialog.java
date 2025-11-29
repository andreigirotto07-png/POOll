package com.locadora.app.ui;

import com.locadora.app.model.Usuario;
import com.locadora.app.service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuariosListDialog extends JDialog implements Refreshable {

    private JTable tabela;
    private final UsuarioService service = new UsuarioService();

    public UsuariosListDialog(Frame parent) {
        super(parent, true);

        setTitle("Usuários");
        setSize(600, 400);
        setLocationRelativeTo(parent);

        tabela = new JTable();
        tabela.setAutoCreateRowSorter(true); // ✔ ordenação automática

        JButton btnAdd = new JButton("Adicionar");
        JButton btnEdit = new JButton("Editar");
        JButton btnDel = new JButton("Excluir");

        btnAdd.addActionListener(e -> abrirForm(null));
        btnEdit.addActionListener(e -> editar());
        btnDel.addActionListener(e -> excluir());

        JPanel botoes = new JPanel();
        botoes.add(btnAdd);
        botoes.add(btnEdit);
        botoes.add(btnDel);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        refresh();
    }

    @Override
    public void refresh() {
        try {
            List<Usuario> lista = service.listar();

            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Email", "Telefone"}, 0
            );

            for (Usuario u : lista) {
                model.addRow(new Object[]{
                        u.getId(),
                        u.getNome(),
                        u.getEmail(),
                        u.getTelefone()
                });
            }

            tabela.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void abrirForm(Usuario u) {
        // agora funciona perfeitamente pois o construtor aceita Window
        new UsuarioFormDialog(this, u, this::refresh).setVisible(true);
    }

    private void editar() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário");
            return;
        }

        Usuario u = new Usuario();
        u.setId(((Number) tabela.getValueAt(row, 0)).intValue());
        u.setNome((String) tabela.getValueAt(row, 1));
        u.setEmail((String) tabela.getValueAt(row, 2));
        u.setTelefone((String) tabela.getValueAt(row, 3));

        abrirForm(u);
    }

    private void excluir() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário");
            return;
        }

        try {
            int id = ((Number) tabela.getValueAt(row, 0)).intValue();
            service.remover(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
