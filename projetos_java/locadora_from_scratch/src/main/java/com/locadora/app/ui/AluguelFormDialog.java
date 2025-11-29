package com.locadora.app.ui;

import com.locadora.app.model.Aluguel;
import com.locadora.app.model.Usuario;
import com.locadora.app.model.Veiculo;
import com.locadora.app.service.AluguelService;
import com.locadora.app.service.UsuarioService;
import com.locadora.app.service.VeiculoService;

import raven.datetime.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AluguelFormDialog extends JDialog {

    private final AluguelService aluguelService = new AluguelService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final VeiculoService veiculoService = new VeiculoService();

    private JComboBox<Usuario> cbUsuario;
    private JComboBox<Veiculo> cbVeiculo;

    private DatePicker dpInicio;   // DatePicker correto
    private JTextField txtKm;

    private final Runnable onSave;

    public AluguelFormDialog(Window parent, Runnable onSave) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.onSave = onSave;

        setTitle("Abrir Aluguel");
        setSize(480, 260);
        setLocationRelativeTo(parent);

        init();
    }

    private void init() {

        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        try {
            cbUsuario = new JComboBox<>(usuarioService.listar().toArray(new Usuario[0]));
            cbVeiculo = new JComboBox<>(veiculoService.listar().toArray(new Veiculo[0]));
        } catch (Exception e) {
            cbUsuario = new JComboBox<>();
            cbVeiculo = new JComboBox<>();
            JOptionPane.showMessageDialog(this, "Erro ao carregar listas: " + e.getMessage());
        }

        dpInicio = new DatePicker();
        dpInicio.setSelectedDate(LocalDate.now()); // <-- método correto

        txtKm = new JTextField("0");

        p.add(new JLabel("Cliente:"));     p.add(cbUsuario);
        p.add(new JLabel("Veículo:"));     p.add(cbVeiculo);
        p.add(new JLabel("Data início:")); p.add(dpInicio);
        p.add(new JLabel("Km Inicial:"));  p.add(txtKm);

        JButton salvar = new JButton("Salvar");
        salvar.addActionListener(e -> salvar());
        p.add(new JPanel());
        p.add(salvar);

        add(p);
    }

    private void salvar() {
        try {

            Usuario u = (Usuario) cbUsuario.getSelectedItem();
            Veiculo v = (Veiculo) cbVeiculo.getSelectedItem();

            if (u == null || v == null)
                throw new IllegalArgumentException("Selecione um cliente e um veículo.");

            LocalDate inicio = dpInicio.getSelectedDate(); // <-- método correto

            if (inicio == null)
                throw new IllegalArgumentException("Data de início inválida.");

            int kmInicio;
            try {
                kmInicio = Integer.parseInt(txtKm.getText().trim());
            } catch (Exception ex) {
                throw new IllegalArgumentException("Quilometragem inicial inválida.");
            }

            Aluguel a = new Aluguel();
            a.setUsuarioId(u.getId());
            a.setVeiculoId(v.getId());
            a.setKmInicio(kmInicio);
            a.setDataInicio(inicio);
            a.setStatus("ABERTO");
            a.setDataFim(null);
            a.setKmFim(null);

            aluguelService.criarAluguel(a);

            JOptionPane.showMessageDialog(this, "Aluguel criado com sucesso!");

            if (onSave != null) onSave.run();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
