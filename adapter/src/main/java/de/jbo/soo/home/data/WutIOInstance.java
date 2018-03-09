//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: WutData.java
// Created: 09.10.2016 - 13:14:09
//
package de.jbo.soo.home.data;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

    private static final String RESPONSE_DOCTYPE = "<!DOCTYPE html>";

    private static final String VALUE_ID_PREFIX_WUT = "wut.";

    private static final String VALUE_ID_PREFIX_OUTPUT = ".output.";

    private static final String VALUE_ID_PREFIX_INPUT = ".input.";

    private static final String VALUE_ID_PREFIX_COUNTER = ".counter.";

    private static final String VALUE_ID_ADDRESS = ".address";

    private String hostAddress;

    private volatile boolean isConnected = false;

    private DataStore dataStore = new DataStore(12);

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

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Wut" + wutIndex + " (" + hostAddress + ")";
    }

    public void connect() {
        LOG.info("Connecting WUT " + wutIndex + " ...");
        connector.addResultListener(this);
        try {
            connector.connect(hostAddress);
            isConnected = connector.isInitialized();

            if (isConnected) {
                LOG.info("Successfully connected.");
            }
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

        // address
        ValueDefinition2 address = new ValueDefinition2(createIdAddress(wutIndex), adapterSpec, createIdAddress(wutIndex));
        address.setDataType(DataType.STRING);
        address.setDefaultValue(hostAddress);
        address.setReadable(true);
        address.setWritable(false);
        address.setValueType(ValueType.URL);
        address.setNativeObjectType("address");
        valueDefinitions.put(address.getId(), address);

        initValueAddress();
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

    private String createIdAddress(int wutIndex) {
        return createIdPrefix(wutIndex) + VALUE_ID_ADDRESS;
    }

    private ValueDefinition2 initValueDefinitionOutput(AdapterSpec2 adapterSpec, int wutIndex, int index) {
        return initValueDefinition(adapterSpec, createIdOutput(wutIndex, index), Integer.toString(index), true, ValueType.VALUE, DataType.INT);
    }

    private ValueDefinition2 initValueDefinitionInput(AdapterSpec2 adapterSpec, int wutIndex, int index) {
        return initValueDefinition(adapterSpec, createIdInput(wutIndex, index), Integer.toString(index), false, ValueType.VALUE, DataType.INT);
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
        definition.setMinValue(0.0);
        definition.setMaxValue(1.0);
        if (valueType.equals(ValueType.ENUM)) {
            Map<Integer, String> enums = new HashMap<>();
            enums.put(InputOutputEnum.ON.ordinal(), InputOutputEnum.ON.name());
            enums.put(InputOutputEnum.OFF.ordinal(), InputOutputEnum.OFF.name());
            definition.setEnums(enums);
        }
        return definition;
    }

    public synchronized Collection<ValueDefinition2> getValueDefinitions() {
        return new ArrayList<ValueDefinition2>(valueDefinitions.values());
    }

    public synchronized boolean isAvailable(Value value) {
        return valueDefinitions.containsKey(value.getId());
    }

    public synchronized boolean isAvailable(String id) {
        return valueDefinitions.containsKey(id);
    }

    public synchronized Value setValue(Value value) {
        ValueDefinition2 def = valueDefinitions.get(value.getId());
        if (def != null) {
            Object newValue = null;
            LOG.debug("Set value '" + value.getId() + "' to '" + value.getValue() + "'");
            int index = Integer.parseInt(def.getNativeObjectType());
            if (value.getId().contains(VALUE_ID_PREFIX_COUNTER)) {
                newValue = Integer.parseInt(value.getValue().toString());
                dataStore.setCounter(index, (Integer) newValue);
            } else if (value.getId().contains(VALUE_ID_PREFIX_INPUT)) {
                newValue = Integer.parseInt(value.getValue().toString());
                dataStore.setInput(index, transformValue((Integer) newValue));
            } else {
                boolean output = Boolean.parseBoolean(value.getValue().toString());
                newValue = transformValue(output);
                dataStore.setOutput(index, output);
                String request = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_OUTPUTACCESS + index, password, IOProcessor.PARAM_STATE, (output == true) ? IOProcessor.OUTPUT_ACCESS_STATE_ON : IOProcessor.OUTPUT_ACCESS_STATE_OFF);
                LOG.debug("     Sending output-access: " + request);
                connector.sendCommand(request);
            }
            Value cachedValue = values.get(value.getId());
            cachedValue.setTimeOfChange(value.getTimeOfChange());
            cachedValue.setValue(newValue);

            dump(Arrays.asList(cachedValue));
            return cachedValue;
        }
        return null;
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
        dump(changedValues);
        return changedValues;
    }

    private void dump(Collection<Value> values) {
        DateFormat formatter = DateFormat.getDateTimeInstance();
        if (!values.isEmpty()) {
            for (Value value : values) {
                StringBuilder buffer = new StringBuilder();
                buffer.append("value id=" + value.getId());
                buffer.append(", time-of-value=" + formatter.format(new Date(value.getTimeOfValue())));
                buffer.append(", time-of-change=" + formatter.format(new Date(value.getTimeOfChange())));
                buffer.append(", value=" + value.getValue());
                LOG.debug(buffer.toString());
            }
        }
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
                value.setTimeOfValue(0l);
                value.setTimeOfChange(0l);
                value.setId(def.getId());
                value.setDataType(def.getDataType());
                value.setStatus(Value.STATUS_GOOD);
                value.setValue(0);
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

            LOG.trace("     Refreshing id '" + id + "' - old = " + previousValue + " - new = " + newValue);
            if ((previousValue == null) || !newValue.equals(previousValue)) {
                value.setTimeOfChange(time);
                changedValues.add(value);
                LOG.trace("     --> value changed!!!");
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

    private void initValueAddress() {
        String idAddress = createIdAddress(wutIndex);
        LOG.info("Initializing '" + idAddress + "'...");
        ValueDefinition2 def = valueDefinitions.get(idAddress);
        Value value = new Value();
        value.setTimeOfValue(0l);
        value.setTimeOfChange(System.currentTimeMillis());
        value.setId(idAddress);
        value.setDataType(def.getDataType());
        value.setStatus(Value.STATUS_GOOD);
        value.setValue(hostAddress);
        values.put(idAddress, value);
    }

    public synchronized Value getValue(String id) {
        if (values.isEmpty()) {
            refreshValues();
        }
        Value value = values.get(id);
        LOG.info("getValue(" + id + ") = " + value);
        return value;
    }

    /*
     * @see
     * de.jbo.soo.home.io.IConnectionResultListener#onResult(java.lang.Object)
     */
    @Override
    public void onResult(Object result) {
        synchronized (this) {
            try {
                String resultString = result.toString();
                if (!resultString.contains(RESPONSE_DOCTYPE)) {
                    LOG.trace("     response: " + result);
                    IOProcessor.fillResponseFromWutToDataStore(result.toString(), dataStore);
                } else {
                    LOG.trace("     response: OK");
                }
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
