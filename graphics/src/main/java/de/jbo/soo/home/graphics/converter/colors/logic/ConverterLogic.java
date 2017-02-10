//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: ConverterLogic.java
// Created: 08.02.2017 - 19:06:41
//
package de.jbo.soo.home.graphics.converter.colors.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingmbh.sphinx.api2d.core.Core;
import de.ingmbh.sphinx.api2d.core.corDrawing;
import de.ingmbh.sphinx.api2d.core.tools.archive.Archive;
import de.ingmbh.sphinx.api2d.core.tools.archive.ArchiveException;
import de.ingmbh.sphinx.api2d.core.tools.archive.FileArchive;

/**
 * @author Josef Baro (jbo) <br>
 * @version 08.02.2017 jbo - created <br>
 */
public class ConverterLogic {
    private static final Logger LOG = LoggerFactory.getLogger(ConverterLogic.class);

    public boolean convert(String filepath, long colorSource, long colorTarget) {
        LOG.info("Converting graphics: " + filepath);

        try {
            FileArchive arc = new FileArchive(Archive.READ, filepath);
            corDrawing drawing = new corDrawing();
            int state = drawing.load(arc);
            arc.close();
            if (state == Core.OK) {
                drawing.walkTree(new ConverterTreeWalker(colorSource, colorTarget), null);
                state = drawing.saveAsGRX(filepath);
                if (state != Core.OK) {
                    throw new ArchiveException(state, "Error while saving");
                }
                LOG.info("Converting graphics successfully finished.");
                return true;
            } else {
                throw new ArchiveException(state, "Error while loading");
            }
        } catch (ArchiveException e) {
            LOG.error("Failed to convert graphics. We abort...", e);
        }

        return false;
    }
}
