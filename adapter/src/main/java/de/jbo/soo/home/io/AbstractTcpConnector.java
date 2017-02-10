//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: AbstractTcpConnector.java
// Created: 05.11.2016 - 09:07:33
//
package de.jbo.soo.home.io;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.11.2016 jbo - created <br>
 */
public abstract class AbstractTcpConnector implements ITcpConnector {
    private volatile boolean isInitialized = false;

    private String address = null;

    private List<IConnectionResultListener> listeners = new ArrayList<>();

    /*
     * @see de.jbo.soo.home.io.ITcpConnector#connect(java.lang.String)
     */
    @Override
    public synchronized void connect(String address) throws ConnectionFailedException {
        this.address = address;
        if (this.address.endsWith("/")) {
            this.address = this.address.substring(0, this.address.length() - 1);
        }
        isInitialized = connectExec(address);
    }

    /*
     * @see de.jbo.soo.home.io.ITcpConnector#getAddress()
     */
    @Override
    public synchronized String getAddress() {
        return address;
    }

    protected abstract boolean connectExec(String address) throws ConnectionFailedException;

    /*
     * @see de.jbo.soo.home.io.ITcpConnector#close()
     */
    @Override
    public synchronized void close() {
        isInitialized = !execClose();
    }

    protected abstract boolean execClose();

    /*
     * @see de.jbo.soo.home.io.ITcpConnector#sendCommand(java.lang.String)
     */
    @Override
    public synchronized void sendCommand(String command) {
        String response;
        try {
            response = execSendCommand(command);
            fireResponseToListeners(response);
        } catch (SendFailedException e) {
            fireExceptionToListeners(e);
        }
    }

    private void fireExceptionToListeners(SendFailedException e) {
        for (IConnectionResultListener listener : listeners) {
            listener.onException(e);
        }
    }

    private void fireResponseToListeners(String response) {
        for (IConnectionResultListener listener : listeners) {
            listener.onResult(response);
        }
    }

    protected abstract String execSendCommand(String command) throws SendFailedException;

    /*
     * @see
     * de.jbo.soo.home.io.ITcpConnector#addResultListener(de.jbo.soo.home.io.
     * IConnectionResultListener)
     */
    @Override
    public synchronized void addResultListener(IConnectionResultListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /*
     * @see
     * de.jbo.soo.home.io.ITcpConnector#removeResultListener(de.jbo.soo.home.io.
     * IConnectionResultListener)
     */
    @Override
    public synchronized void removeResultListener(IConnectionResultListener listener) {
        listeners.remove(listener);
    }

    /*
     * @see de.jbo.soo.home.io.ITcpConnector#isInitialized()
     */
    @Override
    public synchronized boolean isInitialized() {
        return isInitialized;
    }

}
