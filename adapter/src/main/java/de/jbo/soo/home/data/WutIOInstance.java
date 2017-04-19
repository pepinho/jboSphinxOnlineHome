//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: WutData.java
// Created: 09.10.2016 - 13:14:09
//
package de.jbo.soo.home.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingmbh.sphinx.online.common.vo.AdapterSpec2;
import de.ingmbh.sphinx.online.common.vo.DataType;
import de.ingmbh.sphinx.online.common.vo.Value;
import de.ingmbh.sphinx.online.common.vo.ValueDefinition2;
import de.ingmbh.sphinx.online.common.vo.ValueType;
import de.jbo.soo.home.adapter.properties.PropertiesProvider;
import de.jbo.soo.home.connection.wut.data.DataStore;
import de.jbo.soo.home.connection.wut.io.IOProcessor;
import de.jbo.soo.home.connection.wut.io.InvalidFormatException;
import de.jbo.soo.home.connection.wut.io.UnknownCommandException;
import de.jbo.soo.home.io.ConnectionFailedException;
import de.jbo.soo.home.io.IConnectionResultListener;
import de.jbo.soo.home.io.ITcpConnector;
import de.jbo.soo.home.io.http.HttpConnector;

/**
 * @author Josef Baro (jbo) <br>
 * @version 09.10.2016 jbo - created <br>
 */
public class WutIOInstance implements IConnectionResultListener {
    private static final Logger LOG = LoggerFactory.getLogger(WutIOInstance.class);

    enum InputOutputEnum {
        ON, OFF;
    }

    private static final String VALUE_ID_PREFIX_WUT = "wut.";

    private static final String VALUE_ID_PREFIX_OUTPUT = ".output.";

    private static final String VALUE_ID_PREFIX_INPUT = ".input.";

    private static final String VALUE_ID_PREFIX_COUNTER = ".counter.";

    private String hostAddress;

    private volatile boolean isConnected = false;

    private DataStore dataStore = new DataStore(16);

    private Map<String, ValueDefinition2> valueDefinitions = new HashMap<>();

    private Map<Integer, Integer> inputToOutputMapping = new HashMap<>();

    private Map<String, Value> values = new HashMap<>();

    private int wutIndex;

    private ITcpConnector connector = null;

    private String password = IOProcessor.PASSWORD;

    public WutIOInstance(String hostAddress, int index) {
        this.hostAddress = hostAddress;
        this.wutIndex = index;

        connector = new HttpConnector();
    }

    public void connect() {
        LOG.info("Connecting WUT " + wutIndex + " ...");
        connector.addResultListener(this);
        try {
            connector.connect(hostAddress);
            isConnected = true;
            LOG.info("Successfully connected.");
        } catch (ConnectionFailedException e) {
            isConnected = false;
            LOG.error("Failed connecting.", e);
        }
    }

    public void close() {
        LOG.info("Closing WUT + " + wutIndex + " ...");
        isConnected = false;
        connector.close();
        connector.removeResultListener(this);
    }

    public synchronized void initDataPoints(PropertiesProvider propertiesProvider, Map<String, String> properties, AdapterSpec2 adapterSpec) {
        inputToOutputMapping.clear();
        for (int index = 0; index < dataStore.size(); index++) {
            Integer mappedInput = propertiesProvider.getInputToOutputMappingValue(properties, wutIndex, index);
            if (mappedInput != null) {
                LOG.info("Mapping output '" + index + "' to input '" + mappedInput + "'");
                inputToOutputMapping.put(index, mappedInput);
            }
        }
        valueDefinitions.clear();

        for (int index = 0; index < dataStore.size(); index++) {
            // output
            ValueDefinition2 valueDef = initValueDefinitionOutput(adapterSpec, wutIndex, index);
            valueDefinitions.put(valueDef.getId(), valueDef);

            valueDef = initValueDefinitionInput(adapterSpec, wutIndex, index);
            valueDefinitions.put(valueDef.getId(), valueDef);

            // counter
            valueDef = initValueDefinitionCounter(adapterSpec, wutIndex, index);
            valueDefinitions.put(valueDef.getId(), valueDef);
        }
    }

    /**
     * @return the isConnected
     */
    public final boolean isConnected() {
        return isConnected;
    }

    private String createIdPrefix(int wutIndex) {
        return VALUE_ID_PREFIX_WUT + wutIndex;
    }

    private String createIdInput(int wutIndex, int index) {
        return createIdPrefix(wutIndex) + VALUE_ID_PREFIX_INPUT + index;
    }

    private String createIdOutput(int wutIndex, int index) {
        return createIdPrefix(wutIndex) + VALUE_ID_PREFIX_OUTPUT + index;
    }

    private String createIdCounter(int wutIndex, int index) {
        return createIdPrefix(wutIndex) + VALUE_ID_PREFIX_COUNTER + index;
    }

    private ValueDefinition2 initValueDefinitionOutput(AdapterSpec2 adapterSpec, int wutIndex, int index) {
        return initValueDefinition(adapterSpec, createIdOutput(wutIndex, index), Integer.toString(index), true, ValueType.ENUM, DataType.INT);
    }

    private ValueDefinition2 initValueDefinitionInput(AdapterSpec2 adapterSpec, int wutIndex, int index) {
        return initValueDefinition(adapterSpec, createIdInput(wutIndex, index), Integer.toString(index), false, ValueType.ENUM, DataType.INT);
    }

    private ValueDefinition2 initValueDefinitionCounter(AdapterSpec2 adapterSpec, int wutIndex, int index) {
        return initValueDefinition(adapterSpec, createIdCounter(wutIndex, index), Integer.toString(index), false, ValueType.VALUE, DataType.INT);
    }

    private ValueDefinition2 initValueDefinition(AdapterSpec2 adapterSpec, String id, String idNative, boolean writable, ValueType valueType, DataType dataType) {
        ValueDefinition2 definition = new ValueDefinition2(id, adapterSpec, idNative);
        definition.setDataType(dataType);
        definition.setDefaultValue(0);
        definition.setReadable(true);
        definition.setWritable(writable);
        definition.setValueType(valueType);
        definition.setNativeObjectType(idNative);
        if (valueType.equals(ValueType.ENUM)) {
            Map<Integer, String> enums = new HashMap<>();
            enums.put(InputOutputEnum.ON.ordinal(), InputOutputEnum.ON.name());
            enums.put(InputOutputEnum.OFF.ordinal(), InputOutputEnum.OFF.name());
            definition.setEnums(enums);
            definition.setMinValue(0.0);
            definition.setMaxValue(1.0);
        }
        return definition;
    }

    public synchronized Collection<ValueDefinition2> getValueDefinitions() {
        return new ArrayList<ValueDefinition2>(valueDefinitions.values());
    }

    public synchronized void setValues(Collection<Value> values) {
        for (Value value : values) {
            ValueDefinition2 def = valueDefinitions.get(value.getId());
            if (def != null) {
                LOG.debug("Set value '" + value.getId() + "' to '" + value.getValue() + "'");
                int index = Integer.parseInt(def.getNativeObjectType());
                if (value.getId().contains(VALUE_ID_PREFIX_COUNTER)) {
                    dataStore.setCounter(index, Integer.parseInt(value.getValue().toString()));
                } else if (value.getId().contains(VALUE_ID_PREFIX_INPUT)) {
                    dataStore.setInput(index, Boolean.parseBoolean(value.getValue().toString()));
                } else {
                    boolean output = transformValue(Integer.parseInt(value.getValue().toString()));
                    dataStore.setOutput(index, output);
                    String request = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_OUTPUTACCESS + index, password, IOProcessor.PARAM_STATE, (output == true) ? IOProcessor.OUTPUT_ACCESS_STATE_ON : IOProcessor.OUTPUT_ACCESS_STATE_OFF);
                    LOG.debug("Sending output-access: " + request);
                    connector.sendCommand(request);
                }
                value.setTimeOfChange(System.currentTimeMillis());
                this.values.put(value.getId(), value);
            }
        }
    }

    private Set<String> getKeys(String substring, Set<String> originalKeys) {
        Set<String> keys = new HashSet<>();
        for (String key : originalKeys) {
            if (key.contains(substring)) {
                keys.add(key);
            }
        }
        return keys;
    }

    public synchronized Collection<Value> refreshValues() {
        refreshWutDataStore();

        Set<Value> changedValues = new HashSet<>();
        long time = System.currentTimeMillis();
        refreshValuesSubset(VALUE_ID_PREFIX_COUNTER, changedValues, time);
        refreshValuesSubset(VALUE_ID_PREFIX_OUTPUT, changedValues, time);
        refreshValuesSubset(VALUE_ID_PREFIX_INPUT, changedValues, time);
        LOG.debug(changedValues.toString());
        return changedValues;
    }

    /**
     * @param changedValues
     * @param time
     */
    private void refreshValuesSubset(String keySubstring, Set<Value> changedValues, long time) {
        for (String id : getKeys(keySubstring, valueDefinitions.keySet())) {
            ValueDefinition2 def = valueDefinitions.get(id);
            Value value = this.values.get(id);
            if (value == null) {
                value = new Value();
                value.setTimeOfValue(time);
                value.setId(def.getId());
                value.setDataType(def.getDataType());
            }

            int index = Integer.parseInt(def.getNativeObjectType());
            Object newValue = null;
            if (value.getId().contains(VALUE_ID_PREFIX_COUNTER)) {
                newValue = (dataStore.getCounter(index));
            } else if (value.getId().contains(VALUE_ID_PREFIX_INPUT)) {
                newValue = transformValue(dataStore.getInput(index));
            } else {
                boolean storedValue = dataStore.getOutput(index);
                if (inputToOutputMapping.containsKey(index)) {
                    if (storedValue == false) {
                        storedValue = dataStore.getInput(inputToOutputMapping.get(index));
                    }
                }
                newValue = transformValue(storedValue);

            }
            Object previousValue = value.getValue();
            value.setValue(newValue);

            LOG.trace("Refreshing id '" + id + "' - old = " + previousValue + " - new = " + newValue);
            if ((previousValue == null) || !newValue.equals(previousValue)) {
                value.setTimeOfChange(time);
                changedValues.add(value);
                LOG.trace("   --> value changed!!!");
            }
            this.values.put(id, value);
        }
    }

    private int transformValue(boolean value) {
        return (value) ? 1 : 0;
    }

    private boolean transformValue(int value) {
        if (value == 1) {
            return true;
        }
        return false;
    }

    private synchronized void refreshWutDataStore() {
        if (connector.isInitialized()) {
            String request = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_INPUT, password);
            connector.sendCommand(request);
            request = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_OUTPUT, password);
            connector.sendCommand(request);
            request = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_COUNTER, password);
            connector.sendCommand(request);
        }
    }

    public synchronized Collection<Value> getValues(Collection<String> ids) {
        if (values.isEmpty()) {
            refreshValues();
        }

        List<Value> values = new ArrayList<>();
        for (String id : ids) {
            if (values.contains(id)) {
                values.add(this.values.get(id));
            }
        }
        return values;
    }

    /*
     * @see
     * de.jbo.soo.home.io.IConnectionResultListener#onResult(java.lang.Object)
     */
    @Override
    public void onResult(Object result) {
        synchronized (this) {
            try {
                LOG.debug("response: " + result);
                IOProcessor.fillResponseFromWutToDataStore(result.toString(), dataStore);
            } catch (InvalidFormatException | UnknownCommandException e) {
                LOG.error("Error parsing response from WUT: " + result, e);
            }
        }

    }

    /*
     * @see de.jbo.soo.home.io.IConnectionResultListener#onException(java.lang.
     * Exception)
     */
    @Override
    public void onException(Exception exception) {
        LOG.error("Exception on command to WUT.", exception);
    }
}
