//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: data
// File: DataStoreTest.java
// Created: 17.07.2016 - 09:44:22
//
package de.jbo.soo.home.connection.wut.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Test;

/**
 * @author Josef Baro (jbo) <br>
 * @version 17.07.2016 jbo - created <br>
 */
public class DataStoreTest {
    private static final String FILENAME = "DataStore.xml";

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        File file = new File(FILENAME);
        if (file.exists()) {
            file.delete();
        }
    }

    private InputStream getInputStream(String fileName) {
        return getClass().getResourceAsStream(fileName);
    }

    private InputStream getInputStream() throws FileNotFoundException {
        File file = new File(FILENAME);
        return new BufferedInputStream(new FileInputStream(file));
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        File file = new File(FILENAME);
        return new FileOutputStream(file);
    }

    @Test
    public void testInitializiaton() {
        final int size = 12;
        DataStore data = new DataStore(size);

        assertEquals("The size doesn't match.", size, data.size());

        for (int index = 0; index < size; index++) {
            assertFalse("The input doesn't match.", data.getInput(index));
            assertFalse("The output doesn't match.", data.getOutput(index));
            assertEquals("The counter doesn't match.", 0, data.getCounter(index));
        }
    }

    @Test
    public void test_setting_and_getting_outputs() {
        final int size = 2;
        DataStore data = new DataStore(size);

        data.setOutput(0, true);
        data.setOutput(1, true);

        assertTrue("The output doesn't match.", data.getOutput(0));
        assertTrue("The output doesn't match.", data.getOutput(1));

        data.setOutput(1, false);

        assertFalse("The output doesn't match.", data.getOutput(1));

        assertFalse("The value doesn't match.", data.getOutput(size));
        assertFalse("The value doesn't match.", data.getOutput(-1));

        // no exception is happening on invalid indices...
        data.setOutput(-1, true);
        data.setOutput(11, false);
    }

    @Test
    public void test_setting_and_getting_inputs() {
        final int size = 2;
        DataStore data = new DataStore(size);

        data.setInput(0, true);
        data.setInput(1, true);

        assertTrue("The input doesn't match.", data.getInput(0));
        assertTrue("The input doesn't match.", data.getInput(1));

        data.setInput(1, false);

        assertFalse("The input doesn't match.", data.getInput(1));

        assertFalse("The value doesn't match.", data.getInput(size));
        assertFalse("The value doesn't match.", data.getInput(-1));

        // no exception is happening on invalid indices...
        data.setInput(-1, true);
        data.setInput(11, false);
    }

    @Test
    public void test_setting_and_getting_counters() {
        final int size = 2;
        DataStore data = new DataStore(size);

        data.setCounter(0, 47);
        data.setCounter(1, 11);

        assertEquals("The value doesn't match.", 47, data.getCounter(0));
        assertEquals("The value doesn't match.", 11, data.getCounter(1));

        data.setCounter(1, 24);

        assertEquals("The value doesn't match.", 24, data.getCounter(1));

        // invalid indices return default values on getters and don't cause
        // exceptions on setters
        assertEquals("The value doesn't match.", 0, data.getCounter(-1));
        assertEquals("The value doesn't match.", 0, data.getCounter(11));
        data.setCounter(-1, 1);
        data.setCounter(11, 11);
    }

    @Test
    public void test_save_to_file_and_load_again() throws FileNotFoundException {
        final int size = 2;
        DataStore data = new DataStore(size);

        data.setCounter(0, 1);
        data.setInput(0, true);
        data.setOutput(1, true);

        assertTrue("The file wasn't saved.", data.save(getOutputStream()));

        data = new DataStore();
        assertTrue("The file wasn't loaded.", data.load(getInputStream()));

        assertEquals("The size doesn't match.", 2, data.size());
        assertTrue("The value doesn't match.", data.getInput(0));
        assertFalse("The value doesn't match.", data.getInput(1));
        assertFalse("The value doesn't match.", data.getOutput(0));
        assertTrue("The value doesn't match.", data.getOutput(1));
        assertEquals("The value doesn't match.", 1, data.getCounter(0));
        assertEquals("The value doesn't match.", 0, data.getCounter(1));
    }

    @Test
    public void test_load_invalid_format() {
        DataStore data = new DataStore();
        assertFalse(data.load(getInputStream("data-store-invalid-format.xml")));
    }

    @Test
    public void test_load_invalid_format_size() {
        DataStore data = new DataStore();
        assertFalse(data.load(getInputStream("data-store-invalid-data-size.xml")));
    }

    @Test
    public void test_load_invalid_missing_size() {
        DataStore data = new DataStore();
        assertFalse(data.load(getInputStream("data-store-invalid-missing-size.xml")));
    }

    @Test
    public void test_load_invalid_key_index() {
        DataStore data = new DataStore();
        assertFalse(data.load(getInputStream("data-store-invalid-data-key-index.xml")));
    }

    @Test
    public void test_load_invalid_key() {
        DataStore data = new DataStore();
        assertFalse(data.load(getInputStream("data-store-invalid-data-key.xml")));
    }

    @Test
    public void test_load_invalid_counter_value() {
        DataStore data = new DataStore();
        assertFalse(data.load(getInputStream("data-store-invalid-data-counter.xml")));
    }
}
