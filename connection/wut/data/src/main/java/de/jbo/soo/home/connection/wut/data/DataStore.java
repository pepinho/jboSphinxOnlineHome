//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: data
// File: DataStore.java
// Created: 17.07.2016 - 08:22:03
//
package de.jbo.soo.home.connection.wut.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Josef Baro (jbo) <br>
 * @version 17.07.2016 jbo - created <br>
 */
public class DataStore {
    private static final Logger LOG = LoggerFactory.getLogger(DataStore.class);

    private static final String PROPERTY_KEY_OUTPUT = "output";

    private static final String PROPERTY_KEY_INPUT = "input";

    private static final String PROPERTY_KEY_COUNTER = "counter";

    private static final String PROPERTY_KEY_SIZE = "size";

    private int size = 0;

    private List<Boolean> valuesOutput = null;

    private List<Boolean> valuesInput = null;

    private List<Integer> valuesCounter = null;

    public DataStore() {
        valuesOutput = new ArrayList<Boolean>();
        valuesInput = new ArrayList<Boolean>();
        valuesCounter = new ArrayList<Integer>();
    }

    public DataStore(int size) {
        this();
        initLists(size);
    }

    public void clear() {
        initLists(size);
    }

    /**
     * @param size
     */
    private synchronized void initLists(int size) {
        this.size = size;

        init(valuesOutput, Boolean.FALSE);
        init(valuesInput, Boolean.FALSE);
        init(valuesCounter, 0);
    }

    private void init(List<Boolean> list, Boolean initValue) {
        for (int i = 0; i < size; i++) {
            if (list.size() <= i) {
                list.add(initValue);
            } else {
                list.set(i, initValue);
            }
        }
    }

    private void init(List<Integer> list, Integer initValue) {
        for (int i = 0; i < size; i++) {
            if (list.size() <= i) {
                list.add(initValue);
            } else {
                list.set(i, initValue);
            }
        }
    }

    public void setInput(int index, Boolean value) {
        set(index, valuesInput, value);
    }

    private synchronized void set(int index, List<Boolean> list, Boolean value) {
        if ((index >= 0) && (index < size)) {
            list.set(index, value);
        }
    }

    private synchronized boolean get(int index, List<Boolean> list) {
        if ((index >= 0) && (index < size)) {
            return list.get(index);
        }
        return false;
    }

    public boolean getInput(int index) {
        return get(index, valuesInput);
    }

    public void setOutput(int index, Boolean value) {
        set(index, valuesOutput, value);
    }

    public boolean getOutput(int index) {
        return get(index, valuesOutput);
    }

    public synchronized void setCounter(int index, int value) {
        if ((index >= 0) && (index < size)) {
            valuesCounter.set(index, value);
        }
    }

    public synchronized int getCounter(int index) {
        if ((index >= 0) && (index < size)) {
            return valuesCounter.get(index);
        }
        return 0;
    }

    public synchronized int size() {
        return size;
    }

    public synchronized boolean load(InputStream in) {
        boolean ret = false;
        Properties properties = new Properties();
        try {
            properties.loadFromXML(in);
            parseProperties(properties);
            ret = true;
        } catch (IOException e) {
            LOG.error("Loading the datastore failed.", e);
        } catch (IllegalDataStoreFormatException idsex) {
            LOG.error("The datastore file has an invalid format. Loading it failed.", idsex);
        }
        return ret;
    }

    /**
     * @param properties
     */
    void parseProperties(Properties properties) throws IllegalDataStoreFormatException {
        String property = properties.getProperty(PROPERTY_KEY_SIZE);

        if (property == null) {
            throw new IllegalDataStoreFormatException("Property '" + PROPERTY_KEY_SIZE + "' was not found.", null);
        }
        /*
         * size...
         */
        try {
            size = Integer.parseInt(property);
            initLists(size);
        } catch (NumberFormatException nfex) {
            throw new IllegalDataStoreFormatException("Property '" + PROPERTY_KEY_SIZE + "' has the wrong format.", nfex);
        }
        /*
         * inputs...
         */
        parsePropertiesBooleanToList(properties, PROPERTY_KEY_INPUT, valuesInput);
        /*
         * outputs...
         */
        parsePropertiesBooleanToList(properties, PROPERTY_KEY_OUTPUT, valuesOutput);
        /*
         * counters...
         */
        parsePropertiesIntegerToList(properties, PROPERTY_KEY_COUNTER, valuesCounter);
    }

    int extractIndexFromKey(String prefix, String key) throws IllegalDataStoreFormatException {
        if (key.length() <= prefix.length()) {
            throw new IllegalDataStoreFormatException("The property-key '" + key + "' is not of the correct format '<prefix><index>'", null);
        }
        String suffix = key.substring(prefix.length());
        try {
            return Integer.parseInt(suffix);
        } catch (NumberFormatException nfex) {
            throw new IllegalDataStoreFormatException("The property-key '" + key + "' is not of the correct format '<prefix><index>'", null);
        }
    }

    void parsePropertiesBooleanToList(Properties properties, String property, List<Boolean> list) throws IllegalDataStoreFormatException {
        for (Object key : properties.keySet()) {
            String keyString = key.toString();
            if (keyString.contains(property)) {
                int index = extractIndexFromKey(property, keyString);
                list.set(index, Boolean.parseBoolean(properties.getProperty(keyString)));
            }
        }
    }

    void parsePropertiesIntegerToList(Properties properties, String property, List<Integer> list) throws IllegalDataStoreFormatException {
        for (Object key : properties.keySet()) {
            String keyString = key.toString();
            if (keyString.contains(property)) {
                int index = extractIndexFromKey(property, keyString);
                String valueString = properties.getProperty(keyString);
                try {
                    Integer valueInt = Integer.parseInt(valueString);
                    list.set(index, valueInt);
                } catch (NumberFormatException nfex) {
                    throw new IllegalDataStoreFormatException("Property '" + keyString + "' has the wrong format.", nfex);
                }
            }
        }
    }

    private void setProperties(Properties properties, List<? extends Object> list, String property) {
        int count = 0;
        for (Object value : list) {
            properties.setProperty(property + count++, value.toString());
        }
    }

    public synchronized boolean save(OutputStream out) {
        StringBuilder comments = new StringBuilder();
        Properties properties = new Properties();
        boolean ret = true;

        comments.append("WUT data-store\n");
        comments.append("Stored: " + size + " inputs, outputs and counters.\n");
        comments.append("Changed: " + new SimpleDateFormat().format(new Date()));

        properties.setProperty(PROPERTY_KEY_SIZE, Integer.toString(size));
        setProperties(properties, valuesInput, PROPERTY_KEY_INPUT);
        setProperties(properties, valuesOutput, PROPERTY_KEY_OUTPUT);
        setProperties(properties, valuesCounter, PROPERTY_KEY_COUNTER);

        try {
            properties.storeToXML(out, comments.toString());
        } catch (IOException e) {
            LOG.error("Saving the datastore failed.", e);
            ret = false;
        }
        return ret;
    }
}
