package com.locadora.app.ui;

import com.locadora.app.model.Veiculo;
import com.locadora.app.service.VeiculoService;
import com.locadora.app.ui.render.ColorCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class VeiculosListDialog extends JDialog {

    private JTable tabela;
    private final VeiculoService service = new VeiculoService();

    // Paleta de cores usada pelo ColorCellRenderer
    private final Map<String, Color> palette = Map.of(
            "vermelho", new Color(0xC62828),
            "azul", new Color(0x1565C0),
            "preto", Color.BLACK,
            "branco", Color.WHITE,
            "cinza", new Color(0x757575),
            "prata", new Color(0xBDBDBD)
    );

    public VeiculosListDialog(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);

        setTitle("Veículos");
        setSize(700, 450);
        setLocationRelativeTo(parent);

        tabela = new JTable();
        tabela.setAutoCreateRowSorter(true);   // habilita ordenação por coluna

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
            ) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            for (Veiculo v : list) {
                m.addRow(new Object[]{
                    v.getId(),
                    v.getPlaca(),
                    v.getMarca(),
                    v.getModelo(),
                    v.getAno(),
                    v.getCor() == null ? "" : v.getCor().toLowerCase(),  // <- usado pelo renderer
                    v.isDisponivel(),
                    v.getValorDiaria()
                });
            }

            tabela.setModel(m);

            // APLICA O RENDERER NA COLUNA "Cor" (índice 5)
            tabela.getColumnModel()
                    .getColumn(5)
                    .setCellRenderer(new ColorCellRenderer(palette));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
        v.setId(((Number) tabela.getValueAt(r, 0)).intValue());
        v.setPlaca((String) tabela.getValueAt(r, 1));
        v.setMarca((String) tabela.getValueAt(r, 2));
        v.setModelo((String) tabela.getValueAt(r, 3));

        Object anoObj = tabela.getValueAt(r, 4);
        v.setAno(anoObj instanceof Number ? ((Number) anoObj).intValue() : null);

        v.setCor((String) tabela.getValueAt(r, 5));

        Object dispObj = tabela.getValueAt(r, 6);
        v.setDisponivel(dispObj instanceof Boolean ? (Boolean) dispObj : Boolean.TRUE);

        Object valObj = tabela.getValueAt(r, 7);
        v.setValorDiaria(valObj instanceof Number ? ((Number) valObj).doubleValue() : null);

        abrirForm(v);
    }

    private void excluir() {
        int r = tabela.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo");
            return;
        }

        try {
            Object idObj = tabela.getValueAt(r,0);
            int id = idObj instanceof Number ? ((Number) idObj).intValue() : Integer.parseInt(String.valueOf(idObj));
            service.remover(id);
            carregar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
