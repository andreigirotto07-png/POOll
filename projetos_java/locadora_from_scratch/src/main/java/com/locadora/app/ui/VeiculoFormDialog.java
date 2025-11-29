package com.locadora.app.ui;

import com.locadora.app.model.Veiculo;
import com.locadora.app.service.VeiculoService;
import com.locadora.app.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;

public class VeiculoFormDialog extends JDialog {

    private Veiculo veiculo;
    private final Runnable onSave;
    private final VeiculoService service = new VeiculoService();

    private JTextField txtPlaca, txtMarca, txtModelo, txtAno, txtCor, txtValor;

    public VeiculoFormDialog(Window parent, Veiculo veiculo, Runnable onSave) {
        super(parent, ModalityType.APPLICATION_MODAL);

        this.veiculo = veiculo;
        this.onSave = onSave;

        setTitle(veiculo == null ? "Novo Veículo" : "Editar Veículo");
        setSize(400, 320);
        setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridLayout(7, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        txtPlaca = new JTextField(veiculo != null && veiculo.getPlaca() != null ? veiculo.getPlaca() : "");
        txtMarca = new JTextField(veiculo != null && veiculo.getMarca() != null ? veiculo.getMarca() : "");
        txtModelo = new JTextField(veiculo != null && veiculo.getModelo() != null ? veiculo.getModelo() : "");
        txtAno = new JTextField(veiculo != null && veiculo.getAno() != null ? veiculo.getAno().toString() : "");
        txtCor = new JTextField(veiculo != null && veiculo.getCor() != null ? veiculo.getCor() : "");
        txtValor = new JTextField(veiculo != null && veiculo.getValorDiaria() != null ? veiculo.getValorDiaria().toString() : "");

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

            // ---------------------------
            // RT4 — Validações com Regex
            // ---------------------------

            // placa
            String placa = txtPlaca.getText().trim().toUpperCase();
            if (!ValidationUtils.placaValida(placa)) {
                throw new IllegalArgumentException(
                        "Placa inválida!\nFormatos aceitos:\n- ABC-1234\n- ABC1D23 (Mercosul)"
                );
            }

            // marca, modelo
            veiculo.setMarca(txtMarca.getText().trim());
            veiculo.setModelo(txtModelo.getText().trim());

            // ano
            String anoStr = txtAno.getText().trim();
            if (anoStr.isBlank()) {
                veiculo.setAno(null);
            } else if (!anoStr.matches("^\\d{4}$")) {
                throw new IllegalArgumentException("Ano inválido! Exemplo: 2019");
            } else {
                veiculo.setAno(Integer.parseInt(anoStr));
            }

            veiculo.setCor(txtCor.getText().trim());

            // valor diária
            String valorStr = txtValor.getText().trim();

            if (!valorStr.isBlank() && !ValidationUtils.moedaValida(valorStr)) {
                throw new IllegalArgumentException(
                        "Valor inválido!\nExemplos aceitos:\n12.500,90\n12500,90\n12500.90\n12500"
                );
            }

            // converter moeda para formato Double
            if (!valorStr.isBlank()) {
                String convertido = valorStr.replace(".", "").replace(",", ".");
                veiculo.setValorDiaria(Double.parseDouble(convertido));
            } else {
                veiculo.setValorDiaria(null);
            }

            veiculo.setPlaca(placa);

            // salvar
            service.salvar(veiculo);

            if (onSave != null) onSave.run();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
