//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: servlet
// File: IOProcessor.java
// Created: 24.07.2016 - 10:24:48
//
package de.jbo.soo.home.connection.wut.io;

import java.util.ArrayList;
import java.util.List;

import de.jbo.soo.home.connection.wut.data.DataStore;

/**
 * @author Josef Baro (jbo) <br>
 * @version 24.07.2016 jbo - created <br>
 */
public class IOProcessor {
    public static final String RESULT_COMMAND_COUNTER = "counter";

    public static final String RESULT_COMMAND_OUTPUT = "output";

    public static final String RESULT_COMMAND_INPUT = "input";

    private static final String RESULT_COMMAND_SEPARATOR = ";";

    private DataStore dataStore;

    public IOProcessor(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public StringBuffer getResponseOutputs() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(RESULT_COMMAND_OUTPUT);
        buffer.append(RESULT_COMMAND_SEPARATOR);
        return buffer;
    }

    // public static List<Integer> decodeInteger()

    public static List<Boolean> decodeBoolean(int valueIndexStart, int valueIndexEnd, String argument) {
        List<Boolean> values = new ArrayList<>((valueIndexEnd - valueIndexStart) + 1);
        for (int i = valueIndexStart; i <= valueIndexEnd; i++) {
            values.add(decodeBoolean(i, argument));
        }
        return values;
    }

    public static boolean decodeBoolean(int valueIndex, String argument) {
        return ((Long.parseLong(argument, 16) & (long) Math.pow(2, valueIndex)) > 0);
    }
}
