//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: io
// File: InvalidFormatException.java
// Created: 24.09.2016 - 10:36:49
//
package de.jbo.soo.home.connection.wut.io;

/**
 * @author Josef Baro (jbo) <br>
 * @version 24.09.2016 jbo - created <br>
 */
public class InvalidFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidFormatException(String value) {
        super("The value has an invalid format: " + value);
    }
}
