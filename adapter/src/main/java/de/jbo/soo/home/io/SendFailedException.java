//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: SendFailedException.java
// Created: 10.07.2016 - 10:18:43
//
package de.jbo.soo.home.io;

/**
 * @author Josef Baro (jbo) <br>
 * @version 10.07.2016 jbo - created <br>
 */
public class SendFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public SendFailedException(String address, String command, Throwable cause) {
        super("Sending failed to: '" + address + "' with command: '" + command + "'", cause);
    }
}
