package com.locadora.app.ui;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Locadora de Veículos");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        criarMenuTema();  // <<--- Adicionamos isso
        criarBotoesPrincipais();
    }

    private void criarBotoesPrincipais() {
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

    /**
     * Menu "Tema" com opções Claro/Escuro.
     */
    private void criarMenuTema() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuTema = new JMenu("Tema");

        JMenuItem itemClaro = new JMenuItem("Claro");
        itemClaro.addActionListener(e -> trocarTemaClaro());

        JMenuItem itemEscuro = new JMenuItem("Escuro");
        itemEscuro.addActionListener(e -> trocarTemaEscuro());

        menuTema.add(itemClaro);
        menuTema.add(itemEscuro);

        menuBar.add(menuTema);
        setJMenuBar(menuBar);
    }

    private void trocarTemaClaro() {
        try {
            FlatLightLaf.setup();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void trocarTemaEscuro() {
        try {
            FlatDarkLaf.setup();
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
