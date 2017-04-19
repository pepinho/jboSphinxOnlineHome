//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-connection-wut-data
// File: SortedKeysProperties.java
// Created: 19.04.2017 - 20:50:55
//
package de.jbo.soo.home.connection.wut.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author Josef Baro (jbo) <br>
 * @version 19.04.2017 jbo - created <br>
 */
public class SortedKeysProperties extends Properties {

    private static final long serialVersionUID = 1L;

    public SortedKeysProperties() {
        super();
    }

    /*
     * @see java.util.Hashtable#keys()
     */
    @Override
    public synchronized Enumeration<Object> keys() {
        return new SortedEnumeration(super.keys());
    }

    private class StringComparator implements Comparator<Object> {

        /*
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }

    }

    private class SortedEnumeration implements Enumeration<Object> {
        private List<Object> keys = new ArrayList<Object>();

        private Iterator<Object> sortedIterator;

        public SortedEnumeration(Enumeration<Object> unsorted) {
            keys.clear();
            while (unsorted.hasMoreElements()) {
                keys.add(unsorted.nextElement());
            }
            Collections.sort(keys, new StringComparator());
            sortedIterator = keys.iterator();
        }

        /*
         * @see java.util.Enumeration#hasMoreElements()
         */
        @Override
        public boolean hasMoreElements() {
            return sortedIterator.hasNext();
        }

        /*
         * @see java.util.Enumeration#nextElement()
         */
        @Override
        public Object nextElement() {
            return sortedIterator.next();
        }

    }
}
