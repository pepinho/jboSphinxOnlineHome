//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: GrxFileFilter.java
// Created: 05.02.2017 - 15:07:30
//
package de.jbo.soo.home.graphics.converter.colors.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.02.2017 jbo - created <br>
 */
public class GrxFileFilter extends FileFilter {
    private static final String ENDING = "grx";

    /*
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(ENDING);
    }

    /*
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return "sphinx open graphics file (*.grx)";
    }

}
