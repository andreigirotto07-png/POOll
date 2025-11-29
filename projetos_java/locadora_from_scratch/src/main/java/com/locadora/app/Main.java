package com.locadora.app;

import com.locadora.app.ui.MainWindow;
import com.locadora.app.util.DB;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Define tema escuro por padrão
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // garante DB e tabelas
        try {
            DB.init();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao iniciar o banco: " + e.getMessage());
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow mw = new MainWindow();
            mw.setVisible(true);
        });
    }
}
