//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: PropertiesProvider.java
// Created: 09.10.2016 - 10:53:05
//
package de.jbo.soo.home.adapter.properties;

import java.text.MessageFormat;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the adapter's properties.
 *
 * @author Josef Baro (jbo) <br>
 * @version 09.10.2016 jbo - created <br>
 */
public class PropertiesProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesProvider.class);

    static final String PREFIX_WUT = "wut";

    static final String KEY_SUFFIX_HOST_ADDRESS = ".host.address";

    static final String KEY_SUFFIX_INPUT_TO_OUTPUT = ".output.";

    static final String KEY_TEMPLATE_HOST_ADDRESS = PREFIX_WUT + "{0}" + KEY_SUFFIX_HOST_ADDRESS;

    static final String KEY_TEMPLATE_INPUT_TO_OUTPUT = PREFIX_WUT + "{0}" + KEY_SUFFIX_INPUT_TO_OUTPUT + "{1}";

    public static final int SUPPORTED_WUT_COUNT = 5;

    public static final int SUPPORTED_INPUT_OUTPUT_COUNT = 16;

    private static final String EMPTY_VALUE = "";

    public void supplyDefault(Map<String, String> properties) {
        LOG.info(properties.toString());
        for (int wutIndex = 1; wutIndex <= PropertiesProvider.SUPPORTED_WUT_COUNT; wutIndex++) {
            String key = getHostAddressKey(wutIndex);
            LOG.info("Checking if exists: " + key);
            if (!properties.containsKey(key)) {
                LOG.info("supplying default-values for WUT " + wutIndex);
                properties.put(key, EMPTY_VALUE);
                for (int outputIndex = 0; outputIndex < PropertiesProvider.SUPPORTED_INPUT_OUTPUT_COUNT; outputIndex++) {
                    key = getInputToOutputMappingKey(wutIndex, outputIndex);
                    if (!properties.containsKey(key)) {
                        properties.put(key, EMPTY_VALUE);
                    }
                }
            } else {
                LOG.info("default-values for WUT " + wutIndex + " are already initialized.");
            }
        }
    }

    /**
     * Returns the host-address for a specific WUT io device.
     *
     * @param properties
     *            The properties to get the value from .
     * @param index
     *            1 to {@value #SUPPORTED_WUT_COUNT}.
     * @return The configured value or null.
     */
    public String getHostAddressValue(Map<String, String> properties, int index) {
        String hostAddressKey = getHostAddressKey(index);
        LOG.info("Getting host-address value for '" + hostAddressKey + "'...");
        String string = properties.get(hostAddressKey);
        return ((string != null) && (string.trim().length() > 0)) ? string : null;
    }

    /**
     * Sets the corresponding value.
     *
     * @param properties
     *            The properties to set the value to .
     * @param index
     *            1 to {@value #SUPPORTED_WUT_COUNT}.
     * @param address
     *            The address to be set.
     */
    public void setHostAddressValue(Map<String, String> properties, int index, String address) {
        properties.put(getHostAddressKey(index), address);
    }

    private void assertIndex(int index, int min, int max) {
        if ((index < min) || (index > max)) {
            throw new IllegalArgumentException("The index must be larger than '" + min + "' and smaller than '" + max + "'.");
        }
    }

    /**
     * Creates the corresponding property-key.
     *
     * @param index
     *            1 to {@value #SUPPORTED_WUT_COUNT}.
     * @return The property-key.
     */
    public String getHostAddressKey(int index) {
        assertIndex(index, 1, SUPPORTED_WUT_COUNT);
        return MessageFormat.format(KEY_TEMPLATE_HOST_ADDRESS, index);
    }

    /**
     * Returns the input-to-output mapping for a specific output of a specific
     * WUT device.
     *
     * @param properties
     *            The properties to get the value from.
     * @param wutIndex
     *            1 to {@value #SUPPORTED_WUT_COUNT}.
     * @param output
     *            0 to {@value #SUPPORTED_INPUT_OUTPUT_COUNT} - 1.
     * @return The corresponding input index
     */
    public Integer getInputToOutputMappingValue(Map<String, String> properties, int wutIndex, int output) {
        String value = properties.get(getInputToOutputMappingKey(wutIndex, output));
        if ((value != null) && (value.trim().length() > 0)) {
            return Integer.parseInt(value);
        }
        return null;
    }

    /**
     * Sets the input-to-output mapping for a specific out of a specific WUT
     * device.
     *
     * @param properties
     *            The properties to set the value to.
     * @param wutIndex
     *            1 to {@link #SUPPORTED_WUT_COUNT}.
     * @param output
     *            0 to {@value #SUPPORTED_INPUT_OUTPUT_COUNT} - 1.
     * @param input
     *            0 to {@value #SUPPORTED_INPUT_OUTPUT_COUNT} - 1.
     */
    public void setInputToOutputMappingValue(Map<String, String> properties, int wutIndex, int output, int input) {
        assertIndex(wutIndex, 1, SUPPORTED_WUT_COUNT);
        assertIndex(output, 0, SUPPORTED_INPUT_OUTPUT_COUNT - 1);
        assertIndex(input, 0, SUPPORTED_INPUT_OUTPUT_COUNT - 1);
        properties.put(getInputToOutputMappingKey(wutIndex, output), Integer.toString(input));
    }

    /**
     * Creates the corresponding property-key.
     *
     * @param wutIndex
     *            1 to {@link #SUPPORTED_WUT_COUNT}.
     * @param output
     *            0 to {@value #SUPPORTED_INPUT_OUTPUT_COUNT} - 1.
     * @return The property-key.
     */
    public String getInputToOutputMappingKey(int wutIndex, int output) {
        assertIndex(wutIndex, 1, SUPPORTED_WUT_COUNT);
        assertIndex(output, 0, SUPPORTED_INPUT_OUTPUT_COUNT - 1);
        return MessageFormat.format(KEY_TEMPLATE_INPUT_TO_OUTPUT, wutIndex, output);
    }
}
