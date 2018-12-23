//
// Copyright (c) 2013 by jbo - Josef Baro
// 
// Project: jbogx2d.base
// File:    StringUtils.java
// Created: 03.11.2013 - 10:06:40
//
package de.jbo.soo.home.lang;

/**
 * String utilities.
 * 
 * @author Josef Baro (jbo)
 * @version 03.11.2013 jbo - created
 */
public final class StringUtils {

    /**
     * Constructor.
     */
    private StringUtils() {

    }

    /**
     * Fills the given number to the buffer. If the nuber is smaller than 10, a
     * leading zero is appended.
     * 
     * @param number
     *            The number to be filled in.
     * @param length
     *            The length to be achieved by filling in leading zeros.
     * @param buf
     *            The buffer to be filled.
     */
    public static void fillWithLeadingZeros(int number, int length, StringBuffer buf) {
        StringBuffer b = new StringBuffer(length);
        b.append(number);
        while (b.length() < length) {
            b.insert(0, 0);
        }
        buf.append(b.toString());
    }

}
