package com.locadora.app.ui;

import com.locadora.app.model.Aluguel;
import com.locadora.app.service.AluguelService;

import raven.datetime.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class FecharAluguelDialog extends JDialog {

    private final Aluguel aluguel;
    private final Runnable onSave;
    private final AluguelService service = new AluguelService();

    private DatePicker dpFim;
    private JTextField txtKmFim;

    public FecharAluguelDialog(Window parent, Aluguel aluguel, Runnable onSave) {
        super(parent, ModalityType.APPLICATION_MODAL);

        this.aluguel = aluguel;
        this.onSave = onSave;

        setTitle("Fechar Aluguel #" + aluguel.getId());
        setSize(400, 250);
        setLocationRelativeTo(parent);

        init();
    }

    private void init() {

        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dpFim = new DatePicker();
        dpFim.setSelectedDate(LocalDate.now()); // <-- método correto

        txtKmFim = new JTextField();

        p.add(new JLabel("Data de Fim:"));
        p.add(dpFim);

        p.add(new JLabel("KM Final:"));
        p.add(txtKmFim);

        JButton btnSave = new JButton("Confirmar Fechamento");
        btnSave.addActionListener(e -> salvar());

        p.add(new JPanel());
        p.add(btnSave);

        add(p);
    }

    private void salvar() {
        try {

            LocalDate fim = dpFim.getSelectedDate(); // <-- método correto

            if (fim == null)
                throw new IllegalArgumentException("Data de fim inválida.");

            if (fim.isBefore(aluguel.getDataInicio()))
                throw new IllegalArgumentException("Data fim não pode ser antes do início.");

            int kmFim;
            try {
                kmFim = Integer.parseInt(txtKmFim.getText().trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("KM final inválido.");
            }

            if (kmFim < aluguel.getKmInicio())
                throw new IllegalArgumentException("KM final não pode ser menor que o inicial.");

            aluguel.setDataFim(fim);
            aluguel.setKmFim(kmFim);
            aluguel.setStatus("FECHADO");

            service.finalizarAluguel(aluguel);

            JOptionPane.showMessageDialog(this,
                    "Aluguel fechado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            if (onSave != null) onSave.run();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao fechar aluguel: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
