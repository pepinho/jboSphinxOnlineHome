//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: PropertiesProviderTest.java
// Created: 09.10.2016 - 11:16:25
//
package de.jbo.soo.home.adapter.properties;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josef Baro (jbo) <br>
 * @version 09.10.2016 jbo - created <br>
 */
public class PropertiesProviderTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void create_host_address_key_for_valid_values() {
        PropertiesProvider provider = new PropertiesProvider();
        int wutIndex = 2;
        String key = provider.getHostAddressKey(wutIndex);
        assertNotNull("The key should not be NULL!", key);
        assertEquals("The key doesn't match!", PropertiesProvider.PREFIX_WUT + wutIndex + PropertiesProvider.KEY_SUFFIX_HOST_ADDRESS, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_host_address_key_for_invalid_values() {
        PropertiesProvider provider = new PropertiesProvider();
        int wutIndex = 0;
        provider.getHostAddressKey(wutIndex);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_host_address_key_for_invalid_values2() {
        PropertiesProvider provider = new PropertiesProvider();
        int wutIndex = PropertiesProvider.SUPPORTED_WUT_COUNT + 1;
        provider.getHostAddressKey(wutIndex);
    }

    @Test
    public void create_input_to_output_mapping_key_for_valid_values() {
        PropertiesProvider provider = new PropertiesProvider();
        int wutIndex = 2;
        int outputIndex = 3;
        String key = provider.getInputToOutputMappingKey(wutIndex, outputIndex);
        assertNotNull("The key should not be NULL!", key);
        assertEquals("The key doesn't match!", PropertiesProvider.PREFIX_WUT + wutIndex + PropertiesProvider.KEY_SUFFIX_INPUT_TO_OUTPUT + outputIndex, key);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_input_to_output_mapping_key_for_invalid_values() {
        PropertiesProvider provider = new PropertiesProvider();
        int wutIndex = -1;
        int outputIndex = 3;
        provider.getInputToOutputMappingKey(wutIndex, outputIndex);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_input_to_output_mapping_key_for_invalid_values2() {
        PropertiesProvider provider = new PropertiesProvider();
        int wutIndex = 1;
        int outputIndex = -3;
        provider.getInputToOutputMappingKey(wutIndex, outputIndex);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_and_getting_host_address_values() {
        Map<String, String> properties = new HashMap<>();
        PropertiesProvider provider = new PropertiesProvider();
        String address = "http://localhost";
        provider.setHostAddressValue(properties, 1, address);
        String value = provider.getHostAddressValue(properties, 1);
        assertNotNull("The value should not be NULL!", value);
        assertEquals("The value doesn't match!", address, value);

        provider.setHostAddressValue(properties, -1, "some address");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_and_getting_input_to_output_mapping_values() {
        Map<String, String> properties = new HashMap<>();
        PropertiesProvider provider = new PropertiesProvider();
        Integer input = 11;
        provider.setInputToOutputMappingValue(properties, 1, 2, input);
        Integer value = provider.getInputToOutputMappingValue(properties, 1, 2);
        assertNotNull("The value should not be NULL!", value);
        assertEquals("The value doesn't match!", input, value);

        value = provider.getInputToOutputMappingValue(properties, 2, 1);
        assertNull("The value should be NULL!", value);

        provider.setInputToOutputMappingValue(properties, 2, -1, 2);
    }

    private void assertKeyExists(String key, Map<String, String> properties) {
        assertTrue("The key should exist: " + key, properties.containsKey(key));
    }

    @Test
    public void provide_default_values() {
        Map<String, String> properties = new HashMap<>();
        PropertiesProvider provider = new PropertiesProvider();
        provider.supplyDefault(properties);

        for (int wutIndex = 1; wutIndex <= PropertiesProvider.SUPPORTED_WUT_COUNT; wutIndex++) {
            String hostAddressKey = provider.getHostAddressKey(wutIndex);
            assertKeyExists(hostAddressKey, properties);

            for (int outputIndex = 0; outputIndex < PropertiesProvider.SUPPORTED_INPUT_OUTPUT_COUNT; outputIndex++) {
                String inputToOutputMappingKey = provider.getInputToOutputMappingKey(wutIndex, outputIndex);
                assertKeyExists(inputToOutputMappingKey, properties);
            }
        }

        assertKeyExists(PropertiesProvider.KEY_USE_SEPARATE_REFRESH_THREADS, properties);
        assertFalse(provider.isUseSeparateRefreshThreads(properties));
        // 2nd init must not overwrite already existing values...
        properties.put(PropertiesProvider.KEY_USE_SEPARATE_REFRESH_THREADS, "true");
        provider.supplyDefault(properties);
        assertTrue(provider.isUseSeparateRefreshThreads(properties));
    }
}
