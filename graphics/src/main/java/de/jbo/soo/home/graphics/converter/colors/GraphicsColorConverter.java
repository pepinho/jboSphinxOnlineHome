//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: GraphicsColorConverter.java
// Created: 05.02.2017 - 10:23:36
//
package de.jbo.soo.home.graphics.converter.colors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingmbh.sphinx.api2d.core.Core;
import de.jbo.soo.home.graphics.converter.colors.ui.Frame;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.02.2017 jbo - created <br>
 */
public class GraphicsColorConverter {
    private static final Logger LOG = LoggerFactory.getLogger(GraphicsColorConverter.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        GraphicsColorConverter converter = new GraphicsColorConverter(args);
        boolean state = converter.start();

        if (!state) {
            System.exit(-1);
        }
    }

    /**
     *
     */
    public GraphicsColorConverter(String[] args) {

    }

    public boolean start() {
        boolean state = initSphinx();

        if (state) {
            initUI();
            state = true;
        } else {
            LOG.error("Sphinx was not initialzed successfully. We abort...");
        }

        return state;
    }

    /**
     *
     */
    private void initUI() {
        Frame frame = new Frame();
        frame.setVisible(true);
    }

    /**
     * @return
     */
    private boolean initSphinx() {
        boolean state = Core.init();

        return state;
    }

}
