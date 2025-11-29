package com.locadora.app.ui.render;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusIconRenderer extends DefaultTableCellRenderer {

    private final Icon iconAberto;
    private final Icon iconFechado;
    private final Icon iconAtraso;

    public StatusIconRenderer() {
        iconAberto  = loadScaledIcon("/icons/open.png");
        iconFechado = loadScaledIcon("/icons/closed.png");
        iconAtraso  = loadScaledIcon("/icons/late.png");

        setHorizontalAlignment(CENTER);
    }

    /** Redimensiona o ícone para caber perfeitamente na JTable */
    private Icon loadScaledIcon(String path) {
        ImageIcon original = new ImageIcon(getClass().getResource(path));
        Image img = original.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column)
    {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String status = String.valueOf(value).toUpperCase();

        setText(""); // remove texto

        switch (status) {
            case "ABERTO"  -> setIcon(iconAberto);
            case "FECHADO" -> setIcon(iconFechado);
            case "ATRASADO" -> setIcon(iconAtraso);
            default -> setIcon(null);
        }

        return this;
    }
}
