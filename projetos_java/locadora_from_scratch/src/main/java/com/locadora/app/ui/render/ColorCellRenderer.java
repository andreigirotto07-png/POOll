package com.locadora.app.ui.render;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Map;

public class ColorCellRenderer extends DefaultTableCellRenderer {

    private final Map<String, Color> palette;

    public ColorCellRenderer(Map<String, Color> palette) {
        this.palette = palette;
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column)
    {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Color bg = table.getBackground();

        if (value instanceof String s) {
            bg = palette.getOrDefault(s.toLowerCase(), table.getBackground());
        }

        setBackground(isSelected ? table.getSelectionBackground() : bg);
        setText("");
        return this;
    }
}
