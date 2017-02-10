//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: Settings.java
// Created: 05.02.2017 - 15:21:05
//
package de.jbo.soo.home.graphics.converter.colors.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.02.2017 jbo - created <br>
 */
public class Settings {
    private static final String FILENAME = "ColorConverterSettings.properties";

    private static final String KEY_COLORSFILE = "ColorsFile";

    private static final String KEY_LASTGRAPHICS = "LastGraphicsFile";

    private static final String KEY_LASTSOURCECOLOR = "LastSouceColorIndex";

    private static final String KEY_LASTTARGETCOLOR = "LastTargetColorIndex";

    private Properties properties = null;

    public Settings() {
        properties = new Properties();

        try {
            FileInputStream file = new FileInputStream(FILENAME);
            properties.load(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            FileOutputStream file = new FileOutputStream(FILENAME);
            properties.store(file, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getColorsFile() {
        return properties.getProperty(KEY_COLORSFILE);
    }

    public void setColorsFile(String file) {
        properties.setProperty(KEY_COLORSFILE, file);
    }

    public String getLastGraphicsFile() {
        return properties.getProperty(KEY_LASTGRAPHICS);
    }

    public void setLastGraphicsFile(String file) {
        properties.setProperty(KEY_LASTGRAPHICS, file);
    }

    public int getColorSource() {
        String property = properties.getProperty(KEY_LASTSOURCECOLOR);
        if (property == null) {
            return 0;
        }
        return Integer.parseInt(property);
    }

    public void setColorSource(int index) {
        properties.setProperty(KEY_LASTSOURCECOLOR, Integer.toString(index));
    }

    public int getColorTarget() {
        String property = properties.getProperty(KEY_LASTTARGETCOLOR);
        if (property == null) {
            return 0;
        }
        return Integer.parseInt(property);
    }

    public void setColorTarget(int index) {
        properties.setProperty(KEY_LASTTARGETCOLOR, Integer.toString(index));
    }

}
