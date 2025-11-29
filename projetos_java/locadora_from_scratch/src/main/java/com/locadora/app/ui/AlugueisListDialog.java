package com.locadora.app.ui;

import com.locadora.app.model.Aluguel;
import com.locadora.app.service.AluguelService;
import com.locadora.app.ui.render.StatusIconRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AlugueisListDialog extends JDialog {

    private JTable tabela;
    private final AluguelService service = new AluguelService();

    public AlugueisListDialog(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);

        setTitle("Aluguéis");
        setSize(850, 480);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tabela = new JTable();
        tabela.setAutoCreateRowSorter(true);

        JButton btnAdd = new JButton("Novo Aluguel");
        btnAdd.addActionListener(e ->
                new AluguelFormDialog(this, this::carregar).setVisible(true)
        );

        JButton btnClose = new JButton("Fechar Aluguel");
        btnClose.addActionListener(e -> fecharAluguel());

        JButton btnDel = new JButton("Excluir");
        btnDel.addActionListener(e -> excluir());

        JPanel botoes = new JPanel();
        botoes.add(btnAdd);
        botoes.add(btnClose);
        botoes.add(btnDel);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);

        carregar();
    }

    /** Carregar lista */
    private void carregar() {
        try {
            List<Aluguel> lista = service.listar();

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Usuário", "Veículo",
                            "Início", "Fim", "Km Início", "Km Fim", "Status"},
                    0
            ) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };

            for (Aluguel a : lista) {
                modelo.addRow(new Object[]{
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

            tabela.setModel(modelo);

            // Ícones na coluna STATUS
            tabela.getColumnModel()
                    .getColumn(7)
                    .setCellRenderer(new StatusIconRenderer());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar lista: " + e.getMessage());
        }
    }

    /** Excluir */
    private void excluir() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluguel");
            return;
        }

        try {
            int id = Integer.parseInt(String.valueOf(tabela.getValueAt(r, 0)));

            if (JOptionPane.showConfirmDialog(this,
                    "Excluir aluguel " + id + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                service.remover(id);
                carregar();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    /** Fechar aluguel */
    private void fecharAluguel() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluguel aberto");
            return;
        }

        String status = String.valueOf(tabela.getValueAt(r, 7));
        if (!"ABERTO".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Somente aluguéis ABERTOS podem ser fechados.");
            return;
        }

        try {
            int id = Integer.parseInt(String.valueOf(tabela.getValueAt(r, 0)));
            Aluguel a = service.buscarPorId(id);

            new FecharAluguelDialog(this, a, this::carregar).setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
