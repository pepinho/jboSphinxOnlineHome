//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: JColorCombobox.java
// Created: 05.02.2017 - 14:55:02
//
package de.jbo.soo.home.graphics.converter.colors.ui;

import java.awt.Color;

import javax.swing.JComboBox;

import de.ingmbh.sphinx.api2d.core.tools.java.ShortWrapper;
import de.ingmbh.sphinx.api2d.core.tools.sal.SAL;
import de.ingmbh.sphinx.api2d.core.tools.sal.salColor;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.02.2017 jbo - created <br>
 */
public class JColorCombobox extends JComboBox<Color> {

    private static final long serialVersionUID = 1L;

    public JColorCombobox() {
        super();
        setEditable(false);
        setRenderer(new JColorComboboxRenderer());
        init();
    }

    public void init() {

        removeAllItems();

        salColor colorTable = SAL.getColorTable();
        ShortWrapper red = new ShortWrapper();
        ShortWrapper green = new ShortWrapper();
        ShortWrapper blue = new ShortWrapper();

        for (long index = -1; index >= -salColor.MAX_COLORS; index--) {
            colorTable.getColor(index, red, green, blue);
            addItem(new IndexColor(index, red.val, green.val, blue.val));
        }

    }

    public long getSelectedColor() {
        int index = getSelectedIndex();
        long colorIndex = -index - 1;
        return colorIndex;
    }

    private class IndexColor extends Color {

        private static final long serialVersionUID = 1L;

        private long index;

        public IndexColor(long index, short r, short g, short b) {
            super(r, g, b);
            this.index = index;
        }

        /*
         * @see java.awt.Color#toString()
         */
        @Override
        public String toString() {
            return Long.toString(index);
        }
    }
}
