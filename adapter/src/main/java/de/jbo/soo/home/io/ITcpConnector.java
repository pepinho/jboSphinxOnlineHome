//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: ITcpConnector.java
// Created: 10.07.2016 - 10:11:12
//
package de.jbo.soo.home.io;

/**
 * Basic interface for handling TCP connections to a data-source.
 *
 * @author Josef Baro (jbo) <br>
 * @version 10.07.2016 jbo - created <br>
 */
public interface ITcpConnector {
    void connect(String address) throws ConnectionFailedException;

    void close();

    void sendCommand(String command);

    void addResultListener(IConnectionResultListener listener);

    void removeResultListener(IConnectionResultListener listener);

    boolean isInitialized();

    String getAddress();
}
