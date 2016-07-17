//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: data
// File: IllegalDataStoreFormatException.java
// Created: 17.07.2016 - 10:47:32
//
package de.jbo.soo.home.connection.wut.data;

/**
 * @author Josef Baro (jbo) <br>
 * @version 17.07.2016 jbo - created <br>
 */
public class IllegalDataStoreFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    public IllegalDataStoreFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
