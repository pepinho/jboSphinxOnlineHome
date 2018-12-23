//
// Copyright (c) 2004 by jbo - Josef Baro
// 
// Project: jbogx2D
// File: WrapperLong.java
// Created: 28.02.2004 - 19:08:48
//

package de.jbo.soo.home.lang;

/**
 * Implements a wrapper-class for a specific simple data-type. It can be used
 * e.g. for out-parameters in method-calls.
 * 
 * @author Josef Baro (jbo) <br>
 * @version 1.0 28.02.2004: jbo created <br>
 */
public class WrapperString {
    /** The wrapped value. */
    private String value = null;

    /**
     * Creates a new instance wrapping the given value.
     * 
     * @param val
     *            The value to be wrapped.
     */
    public WrapperString(String val) {
        value = val;
    }

    /**
     * Creates a new empty instance.
     */
    public WrapperString() {
        super();
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = false;

        if (obj instanceof WrapperString) {
            equals = ((WrapperString) obj).value == value;
        } else {
            equals = super.equals(obj);
        }

        return equals;
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * @return the value
     */
    public final String getValue() {
        return value;
    }

    /**
     * @param newValue
     *            the value to set
     */
    public final void setValue(String newValue) {
        this.value = newValue;
    }

}