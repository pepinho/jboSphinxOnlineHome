//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: IConnectionResultListener.java
// Created: 10.07.2016 - 10:16:52
//
package de.jbo.soo.home.io;

/**
 * @author Josef Baro (jbo) <br>
 * @version 10.07.2016 jbo - created <br>
 */
public interface IConnectionResultListener {
    void onResult(Object result);

    void onException(Exception exception);
}
