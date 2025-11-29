package com.locadora.app.ui;

import com.locadora.app.model.Usuario;
import com.locadora.app.service.UsuarioService;
import com.locadora.app.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;

public class UsuarioFormDialog extends JDialog {

    private Usuario usuario;
    private final Runnable onSave;
    private final UsuarioService service = new UsuarioService();

    private JTextField txtNome, txtEmail, txtTelefone;

    // *** Agora aceita qualquer Window como pai (Frame, Dialog, JDialog, etc.) ***
    public UsuarioFormDialog(Window parent, Usuario usuario, Runnable onSave) {
        super(parent, ModalityType.APPLICATION_MODAL);

        this.usuario = usuario;
        this.onSave = onSave;

        setTitle(usuario == null ? "Novo Usuário" : "Editar Usuário");
        setSize(350, 220);
        setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));

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

        // --- validações RT4 ---
        if (usuario.getNome().isBlank())
            throw new IllegalArgumentException("Nome obrigatório!");

        if (!ValidationUtils.emailValido(usuario.getEmail()))
            throw new IllegalArgumentException("Email inválido!");

        if (!ValidationUtils.telefoneValido(usuario.getTelefone()))
            throw new IllegalArgumentException("Telefone inválido!\nEx: (54) 99999-1234");

        service.salvar(usuario);

        onSave.run();
        dispose();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}

}
