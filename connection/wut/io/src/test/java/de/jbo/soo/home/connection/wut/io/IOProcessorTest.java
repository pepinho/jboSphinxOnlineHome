//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: io
// File: IOProcessorTest.java
// Created: 01.10.2016 - 12:37:25
//
package de.jbo.soo.home.connection.wut.io;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.jbo.soo.home.connection.wut.data.DataStore;

/**
 * @author Josef Baro (jbo) <br>
 * @version 01.10.2016 jbo - created <br>
 */
public class IOProcessorTest {
    private DataStore dataStore = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dataStore = new DataStore(2);
    }

    @Test
    public void testCreateResponseCounters() {
        dataStore.setCounter(1, 1);
        StringBuffer counters = IOProcessor.createResponseCounters(dataStore);
        Assert.assertEquals("The counter-response doesn't match!", IOProcessor.RESULT_COMMAND_COUNTER + IOProcessor.RESULT_COMMAND_SEPARATOR + 0 + IOProcessor.RESULT_COMMAND_SEPARATOR + 1, counters.toString());
    }

    @Test
    public void testCreateResponseOutputsOneSet() {
        dataStore.setOutput(1, true);
        StringBuffer outputs = IOProcessor.createResponseOutputs(dataStore);
        Assert.assertEquals("The outputs-response doesn't match!", IOProcessor.RESULT_COMMAND_OUTPUT + IOProcessor.RESULT_COMMAND_SEPARATOR + 2, outputs.toString());
    }

    @Test
    public void testCreateResponseOutputsNoneSet() {
        StringBuffer outputs = IOProcessor.createResponseOutputs(dataStore);
        Assert.assertEquals("The outputs-response doesn't match!", IOProcessor.RESULT_COMMAND_OUTPUT + IOProcessor.RESULT_COMMAND_SEPARATOR + 0, outputs.toString());
    }

    @Test
    public void testCreateResponseInputsOneSet() {
        dataStore.setInput(1, true);
        StringBuffer inputs = IOProcessor.createResponseInputs(dataStore);
        Assert.assertEquals("The inputs-response doesn't match!", IOProcessor.RESULT_COMMAND_INPUT + IOProcessor.RESULT_COMMAND_SEPARATOR + 2, inputs.toString());
    }

    @Test
    public void testCreateResponseInputsNoneSet() {
        StringBuffer inputs = IOProcessor.createResponseInputs(dataStore);
        Assert.assertEquals("The inputs-response doesn't match!", IOProcessor.RESULT_COMMAND_INPUT + IOProcessor.RESULT_COMMAND_SEPARATOR + 0, inputs.toString());
    }

    @Test
    public void testFillResponseForOutputsValid() throws InvalidFormatException, UnknownCommandException {
        String response = IOProcessor.RESULT_COMMAND_OUTPUT + IOProcessor.RESULT_COMMAND_SEPARATOR + 2;
        IOProcessor.fillResponseFromWutToDataStore(response, dataStore);
        Assert.assertEquals("Value doesn't match!", false, dataStore.getOutput(0));
        Assert.assertEquals("Value doesn't match!", true, dataStore.getOutput(1));
    }

    @Test(expected = InvalidFormatException.class)
    public void testFillResponseForOutputsInvalid() throws InvalidFormatException, UnknownCommandException {
        String response = IOProcessor.RESULT_COMMAND_OUTPUT + IOProcessor.RESULT_COMMAND_SEPARATOR;
        IOProcessor.fillResponseFromWutToDataStore(response, dataStore);
    }

    @Test(expected = UnknownCommandException.class)
    public void testFillResponseForUnknownCommand() throws InvalidFormatException, UnknownCommandException {
        String response = "unknownCommand" + IOProcessor.RESULT_COMMAND_SEPARATOR + 0;
        IOProcessor.fillResponseFromWutToDataStore(response, dataStore);
    }

    @Test
    public void testFillResponseForInputsValid() throws InvalidFormatException, UnknownCommandException {
        String response = IOProcessor.RESULT_COMMAND_INPUT + IOProcessor.RESULT_COMMAND_SEPARATOR + 2;
        IOProcessor.fillResponseFromWutToDataStore(response, dataStore);
        Assert.assertEquals("Value doesn't match!", false, dataStore.getInput(0));
        Assert.assertEquals("Value doesn't match!", true, dataStore.getInput(1));
    }

    @Test
    public void testFillResponseForCountersValid() throws InvalidFormatException, UnknownCommandException {
        String response = IOProcessor.RESULT_COMMAND_COUNTER + IOProcessor.RESULT_COMMAND_SEPARATOR + 1 + IOProcessor.RESULT_COMMAND_SEPARATOR + 24;
        IOProcessor.fillResponseFromWutToDataStore(response, dataStore);
        Assert.assertEquals("Value doesn't match!", 1, dataStore.getCounter(0));
        Assert.assertEquals("Value doesn't match!", 24, dataStore.getCounter(1));
    }

    @Test
    public void testProcessOutputAccessValid() {
        IOProcessor.processOutputAccess(0, "ON", dataStore);
        IOProcessor.processOutputAccess(1, "OFF", dataStore);
        Assert.assertEquals("Value doesn't match!", true, dataStore.getInput(0));
        Assert.assertEquals("Value doesn't match!", false, dataStore.getInput(1));
    }

    @Test
    public void testProcessOutputAccessInValid() {
        IOProcessor.processOutputAccess(0, "ON", dataStore);
        IOProcessor.processOutputAccess(1, "OFF", dataStore);
        IOProcessor.processOutputAccess(1, null, dataStore);
        Assert.assertEquals("Value doesn't match!", true, dataStore.getInput(0));
        Assert.assertEquals("Value doesn't match!", false, dataStore.getInput(1));
    }

    @Test
    public void testProcessCounterClearSpecific() {
        dataStore.setCounter(0, 24);
        IOProcessor.processCounterClear(0, dataStore);
        Assert.assertEquals("The counter should be 0!", 0, dataStore.getCounter(0));
    }

    @Test
    public void testProcessCounterClear() {
        dataStore.setCounter(0, 24);
        dataStore.setCounter(3, 33);
        IOProcessor.processCounterClear(dataStore);
        Assert.assertEquals("The counter should be 0!", 0, dataStore.getCounter(0));
        Assert.assertEquals("The counter should be 0!", 0, dataStore.getCounter(3));
    }
}
