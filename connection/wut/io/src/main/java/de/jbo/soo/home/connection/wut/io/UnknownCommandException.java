//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: io
// File: UnknownCommandException.java
// Created: 24.09.2016 - 10:41:16
//
package de.jbo.soo.home.connection.wut.io;

/**
 * @author Josef Baro (jbo) <br>
 * @version 24.09.2016 jbo - created <br>
 */
public class UnknownCommandException extends Exception {

    private static final long serialVersionUID = 1L;

    public UnknownCommandException(String command) {
        super("The given command is unknown: " + command);
    }
}
