//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: JColorComboboxRenderer.java
// Created: 05.02.2017 - 15:02:44
//
package de.jbo.soo.home.graphics.converter.colors.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.02.2017 jbo - created <br>
 */
public class JColorComboboxRenderer extends JLabel implements ListCellRenderer<Color> {

    private static final long serialVersionUID = 1L;

    /*
     * @see
     * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.
     * JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends Color> list, Color value, int index, boolean isSelected, boolean cellHasFocus) {
        setOpaque(true);
        setText(value.toString());

        if (isSelected) {
            setBackground(UIManager.getColor("Label.selectedBackround"));
        }
        if (cellHasFocus) {
            setBackground(UIManager.getColor("Label.focusBackround"));
        }

        setIcon(new ColorIcon(value));

        return this;
    }

    private class ColorIcon implements Icon {
        private static final int offset = 4;

        private Color color;

        public ColorIcon(Color color) {
            this.color = color;

        }

        /*
         * @see javax.swing.Icon#paintIcon(java.awt.Component,
         * java.awt.Graphics, int, int)
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x + 1, y + 1, getIconWidth() - offset, getIconHeight() - offset);
            g.setColor(Color.BLACK);
            g.drawRect(x + 1, y + 1, getIconWidth() - offset, getIconHeight() - offset);
        }

        /*
         * @see javax.swing.Icon#getIconWidth()
         */
        @Override
        public int getIconWidth() {
            return 50;
        }

        /*
         * @see javax.swing.Icon#getIconHeight()
         */
        @Override
        public int getIconHeight() {
            return 20;
        }
    }

}
