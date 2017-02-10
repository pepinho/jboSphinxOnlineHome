//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: ConverterTreeWalker.java
// Created: 08.02.2017 - 19:07:50
//
package de.jbo.soo.home.graphics.converter.colors.logic;

import de.ingmbh.sphinx.api2d.core.corElement;
import de.ingmbh.sphinx.api2d.core.corTreeWalker;

/**
 * @author Josef Baro (jbo) <br>
 * @version 08.02.2017 jbo - created <br>
 */
public class ConverterTreeWalker extends corTreeWalker {
    private long colorSource;

    private long colorTarget;

    public ConverterTreeWalker(long colorSource, long colorTarget) {
        super();
        this.colorSource = colorSource;
        this.colorTarget = colorTarget;
        setActionBeforeChilds(true);
        setVisitChilds(true);
    }

    /*
     * @see
     * de.ingmbh.sphinx.api2d.core.corTreeWalker#action(de.ingmbh.sphinx.api2d.
     * core.corElement)
     */
    @Override
    public short action(corElement e) {
        if ((e.getPen() != null) && (e.getPen().getColor() == colorSource)) {
            e.getPen().setColor(colorTarget);
        }
        if ((e.getBrush() != null) && (e.getBrush().getColor() == colorSource)) {
            e.getBrush().setColor(colorTarget);
        }
        if ((e.getFont() != null) && (e.getFont().getColor() == colorSource)) {
            e.getFont().setColor(colorTarget);
        }
        return corTreeWalker.CONTINUE;
    }
}
