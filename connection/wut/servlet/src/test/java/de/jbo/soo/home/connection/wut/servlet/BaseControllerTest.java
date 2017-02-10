//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: servlet
// File: BaseControllerTest.java
// Created: 03.10.2016 - 07:54:22
//
package de.jbo.soo.home.connection.wut.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import de.jbo.soo.home.connection.wut.data.DataStore;
import de.jbo.soo.home.connection.wut.io.IOProcessor;

/**
 * @author Josef Baro (jbo) <br>
 * @version 03.10.2016 jbo - created <br>
 */
public class BaseControllerTest {
    private static final String PASSWORD = IOProcessor.PASSWORD;

    private BaseController baseController = null;

    private ModelMap model = null;

    private DataStore dataStore() {
        DataStore store = BaseController.getDataStore();
        if (store == null) {
            BaseController.validateDataStore();
            store = BaseController.getDataStore();
        }
        return store;
    }

    private void assertDataStoreOutput(int index, boolean expected) {
        assertDataStoreOutput(index, expected, dataStore());
    }

    private void assertDataStoreOutput(int index, boolean expected, DataStore store) {
        assertEquals("The output-value doesn't match!", expected, store.getOutput(index));
    }

    private void assertDataStoreCounter(int index, int expected) {
        assertDataStoreCounter(index, expected, dataStore());
    }

    private void assertDataStoreCounter(int index, int expected, DataStore store) {
        assertEquals("The counter-value doesn't match!", expected, store.getCounter(index));
    }

    private void assertDataStoreCounters(int expected) {
        DataStore store = dataStore();
        for (int index = 0; index < store.size(); index++) {
            assertDataStoreCounter(index, expected, store);
        }
    }

    private void assertModel(String key, Object value) {
        assertEquals("The model-value doesn't match: " + key, value, model.get(key));
    }

    @Before
    public void setUp() throws Exception {
        dataStore().clear();
        baseController = new BaseController();
        model = new ModelMap();
    }

    @After
    public void tearDown() throws Exception {
        BaseController.clearDataStoreFile();
    }

    @Test(expected = IllegalAccessException.class)
    public void clear_counter_all_invalid_password() throws IllegalAccessException {
        baseController.counterclear("password", model);
    }

    @Test
    public void clear_counter_all() throws IllegalAccessException {
        dataStore().setCounter(0, 24);
        baseController.counterclear(PASSWORD, model);
        assertDataStoreCounters(0);
    }

    @Test(expected = IllegalAccessException.class)
    public void clear_counter_specific_invalid_password() throws IllegalAccessException {
        baseController.counterclear("password", 0, model);
    }

    @Test
    public void clear_counter_specific() throws IllegalAccessException {
        dataStore().setCounter(0, 24);
        baseController.counterclear(PASSWORD, 0, model);
        assertDataStoreCounter(0, 0);
    }

    @Test(expected = IllegalAccessException.class)
    public void get_counter_values_invalid_password() throws IllegalAccessException {
        baseController.counters("some password", model);
    }

    @Test
    public void get_counter_values() throws IllegalAccessException {
        dataStore().setCounter(1, 24);
        dataStore().setCounter(15, 11);
        String counters = baseController.counters(PASSWORD, model);
        assertEquals("The counter-values don't match!", IOProcessor.createResponseCounters(dataStore()).toString(), counters);
    }

    @Test
    public void get_index() {
        dataStore().setCounter(1, 24);
        dataStore().setOutput(0, true);
        dataStore().setInput(2, true);
        String ret = baseController.index(model);
        assertEquals("The return-value doesn't match!", BaseController.VIEW_INDEX, ret);
        assertModel(IOProcessor.ATTRIBUTE_PREFIX_COUNTER + 1, 24);
        assertModel(IOProcessor.ATTRIBUTE_PREFIX_INPUT + 2, true);
        assertModel(IOProcessor.ATTRIBUTE_PREFIX_OUTPUT + 0, true);
    }

    @Test(expected = IllegalAccessException.class)
    public void get_input_values_invalid_password() throws IllegalAccessException {
        baseController.inputs("some password", model);
    }

    @Test
    public void get_input_values() throws IllegalAccessException {
        dataStore().setInput(1, true);
        dataStore().setInput(15, true);
        String inputs = baseController.inputs(PASSWORD, model);
        assertEquals("The input-values don't match!", IOProcessor.createResponseInputs(dataStore()).toString(), inputs);
    }

    @Test(expected = IllegalAccessException.class)
    public void get_output_values_invalid_password() throws IllegalAccessException {
        baseController.outputs("some password", model);
    }

    @Test
    public void get_output_values() throws IllegalAccessException {
        dataStore().setOutput(1, true);
        dataStore().setOutput(15, true);
        String outputs = baseController.outputs(PASSWORD, model);
        assertEquals("The output-values don't match!", IOProcessor.createResponseOutputs(dataStore()).toString(), outputs);
    }

    @Test(expected = IllegalAccessException.class)
    public void output_access_invalid_password() throws IllegalAccessException {
        baseController.outputaccess("some password", 0, IOProcessor.OUTPUT_ACCESS_STATE_ON, model);
    }

    @Test
    public void output_access_to_ON() throws IllegalAccessException {
        baseController.outputaccess(PASSWORD, 0, IOProcessor.OUTPUT_ACCESS_STATE_ON, model);
        assertDataStoreOutput(0, true);
    }

    @Test
    public void output_access_to_ON_and_OFF() throws IllegalAccessException {
        baseController.outputaccess(PASSWORD, 0, IOProcessor.OUTPUT_ACCESS_STATE_ON, model);
        assertDataStoreOutput(0, true);
        baseController.outputaccess(PASSWORD, 0, IOProcessor.OUTPUT_ACCESS_STATE_OFF, model);
        assertDataStoreOutput(0, false);
    }
}
