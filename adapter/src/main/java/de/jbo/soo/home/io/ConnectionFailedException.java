//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: ConnectionFailedException.java
// Created: 10.07.2016 - 10:12:44
//
package de.jbo.soo.home.io;

/**
 * @author Josef Baro (jbo) <br>
 * @version 10.07.2016 jbo - created <br>
 */
public class ConnectionFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConnectionFailedException(String address, Throwable cause) {
        super("The connection failed to: '" + address + "'", cause);
    }
}
