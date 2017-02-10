//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: servlet
// File: IOProcessor.java
// Created: 24.07.2016 - 10:24:48
//
package de.jbo.soo.home.connection.wut.io;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;

import de.jbo.soo.home.connection.wut.data.DataStore;

/**
 * @author Josef Baro (jbo) <br>
 * @version 24.07.2016 jbo - created <br>
 */
public class IOProcessor {

    public static final String PARAM_STATE = "STATE";

    public static final String OUTPUT_ACCESS_STATE_ON = "ON";

    public static final String OUTPUT_ACCESS_STATE_OFF = "OFF";

    public static final String RESULT_COMMAND_COUNTER = "counter";

    public static final String RESULT_COMMAND_OUTPUT = "output";

    public static final String RESULT_COMMAND_INPUT = "input";

    static final String RESULT_COMMAND_SEPARATOR = ";";

    private static final int MIN_TOKEN_SIZE = 2;

    public static final String PARAM_PW = "PW";

    public static final String ATTRIBUTE_PREFIX_OUTPUTACCESS = "outputaccess";

    public static final String ATTRIBUTE_PREFIX_OUTPUT = "output";

    public static final String ATTRIBUTE_PREFIX_INPUT = "input";

    public static final String ATTRIBUTE_PREFIX_COUNTER = "counter";

    /**
     *
     */
    public static final String ATTRIBUTE_PREFIX_COUNTERCLEAR = "counterclear";

    public static String PASSWORD = "wut01";

    private IOProcessor() {

    }

    public static void fillResponseFromWutToDataStore(String response, DataStore dataStore) throws InvalidFormatException, UnknownCommandException {
        StringTokenizer tokens = new StringTokenizer(response, RESULT_COMMAND_SEPARATOR);
        if (tokens.countTokens() < MIN_TOKEN_SIZE) {
            throw new InvalidFormatException(response);
        }
        String command = tokens.nextToken();

        if (command.equals(RESULT_COMMAND_INPUT)) {
            fillResponseInputs(tokens.nextToken(), dataStore);
        } else if (command.equals(RESULT_COMMAND_OUTPUT)) {
            fillResponseOutputs(tokens.nextToken(), dataStore);
        } else if (command.equals(RESULT_COMMAND_COUNTER)) {
            fillResponseCounters(tokens, dataStore);
        } else if (command.startsWith("<!")) {
            // default page, ntbd...
        } else {
            throw new UnknownCommandException(command);
        }
    }

    public static void processOutputAccess(int index, String value, DataStore dataStore) {
        if (value == null) {
            return;
        }
        boolean valueBoolean = (value.equals(OUTPUT_ACCESS_STATE_ON)) ? true : false;
        dataStore.setOutput(index, valueBoolean);
    }

    public static void processCounterClear(int index, DataStore dataStore) {
        dataStore.setCounter(index, 0);
    }

    public static void processCounterClear(DataStore dataStore) {
        for (int i = 0; i < dataStore.size(); i++) {
            dataStore.setCounter(i, 0);
        }
    }

    /**
     * @param tokens
     * @param dataStore
     */
    private static void fillResponseCounters(StringTokenizer tokens, DataStore dataStore) {
        int index = 0;
        while (tokens.hasMoreTokens()) {
            dataStore.setCounter(index++, Integer.parseInt(tokens.nextToken()));
        }
    }

    /**
     * @param nextToken
     * @param dataStore
     */
    private static void fillResponseOutputs(String nextToken, DataStore dataStore) {
        List<Boolean> decodeBooleans = decodeBoolean(0, dataStore.size() - 1, nextToken);
        int index = 0;
        for (Boolean value : decodeBooleans) {
            dataStore.setOutput(index++, value);
        }
    }

    /**
     * @param nextToken
     * @param dataStore
     */
    private static void fillResponseInputs(String nextToken, DataStore dataStore) {
        List<Boolean> decodeBooleans = decodeBoolean(0, dataStore.size() - 1, nextToken);
        int index = 0;
        for (Boolean value : decodeBooleans) {
            dataStore.setInput(index++, value);
        }
    }

    public static StringBuffer createResponseOutputs(DataStore dataStore) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(RESULT_COMMAND_OUTPUT);
        buffer.append(RESULT_COMMAND_SEPARATOR);
        List<Boolean> outputs = new ArrayList<>();
        for (int index = 0; index < dataStore.size(); index++) {
            outputs.add(dataStore.getOutput(index));
        }
        buffer.append(encodeBoolean(outputs));
        return buffer;
    }

    public static StringBuffer createResponseInputs(DataStore dataStore) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(RESULT_COMMAND_INPUT);
        buffer.append(RESULT_COMMAND_SEPARATOR);
        List<Boolean> inputs = new ArrayList<>();
        for (int index = 0; index < dataStore.size(); index++) {
            inputs.add(dataStore.getInput(index));
        }
        buffer.append(encodeBoolean(inputs));
        return buffer;
    }

    public static StringBuffer createResponseCounters(DataStore dataStore) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(RESULT_COMMAND_COUNTER);
        buffer.append(RESULT_COMMAND_SEPARATOR);
        buffer.append(dataStore.getCounter(0));
        for (int index = 1; index < dataStore.size(); index++) {
            buffer.append(RESULT_COMMAND_SEPARATOR);
            buffer.append(dataStore.getCounter(index));
        }
        return buffer;
    }

    private static BitSet hexStringToBitSet(String hexString) {
        return BitSet.valueOf(new long[] { Long.parseLong(hexString, 16) });
    }

    public static String encodeBoolean(List<Boolean> values) {
        BitSet bitset = new BitSet(values.size());

        for (int index = 0; index < values.size(); index++) {
            bitset.set(index, values.get(index));
        }
        long[] longArray = bitset.toLongArray();
        long hexLong = ((longArray != null) && (longArray.length > 0)) ? longArray[0] : 0l;
        return Long.toHexString(hexLong);
    }

    public static List<Boolean> decodeBoolean(int valueIndexStart, int valueIndexEnd, String argument) {
        List<Boolean> values = new ArrayList<>((valueIndexEnd - valueIndexStart) + 1);
        BitSet bitset = hexStringToBitSet(argument);
        for (int i = valueIndexStart; i <= valueIndexEnd; i++) {
            values.add(bitset.get(i));
        }
        return values;
    }

    public static String createRequest(String value, String pw, String... params) {
        String request = "/" + value + "?" + PARAM_PW + "=" + pw;
        if ((params != null) && (params.length > 0)) {
            for (int i = 0; i < params.length; i += 2) {
                request += "&" + params[i];
                request += "=";
                request += params[i + 1];
            }
        }
        return request;
    }

}
