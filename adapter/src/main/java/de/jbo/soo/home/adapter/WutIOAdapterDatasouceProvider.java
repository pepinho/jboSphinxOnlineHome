//
// Copyright (c) 2018 by jbo - Josef Baro
//
// Project: jbo-soo-home-adapter
// File: WutIOAdapterDatasouceProvider.java
// Created: 03.03.2018 - 19:19:06
//
package de.jbo.soo.home.adapter;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapter;
import de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapterProvider;
import de.ingmbh.sphinx.online.common.vo.AdapterType;
import de.jbo.soo.home.adapter.properties.PropertiesProvider;

/**
 * @author Josef Baro (jbo) <br>
 * @version 03.03.2018 jbo - created <br>
 */
public class WutIOAdapterDatasouceProvider implements IDataSourceAdapterProvider {
    private static final Logger LOG = LoggerFactory.getLogger(WutIOAdapterDatasouceProvider.class);

    private static final AdapterType WUT_ADAPTER_TYPE = new AdapterType(WutIOAdapter.ADAPTER_TYPE_NAME, WutIOAdapter.ADAPTER_TYPE_DESCRIPTION, WutIOAdapter.class.getName());

    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapterProvider#
     * canSupplyDefaultConfiguration(de.ingmbh.sphinx.online.common.vo.
     * AdapterType)
     */
    @Override
    public boolean canSupplyDefaultConfiguration(AdapterType arg0) {
        return false;
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapterProvider#
     * canSupplyDefaultProperties(de.ingmbh.sphinx.online.common.vo.AdapterType)
     */
    @Override
    public boolean canSupplyDefaultProperties(AdapterType arg0) {
        return true;
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapterProvider#
     * createAdapter(de.ingmbh.sphinx.online.common.vo.AdapterType,
     * java.lang.ClassLoader)
     */
    @Override
    public IDataSourceAdapter createAdapter(AdapterType arg0, ClassLoader arg1) {
        LOG.info("Creating adapter...");
        return new WutIOAdapter(propertiesProvider);
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapterProvider#
     * getSupportedAdapterTypes(java.lang.ClassLoader)
     */
    @Override
    public Collection<AdapterType> getSupportedAdapterTypes(ClassLoader arg0) {
        return Arrays.asList(WUT_ADAPTER_TYPE);
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapterProvider#
     * supplyDefaultProperties(de.ingmbh.sphinx.online.common.vo.AdapterType)
     */
    @Override
    public Map<String, String> supplyDefaultProperties(AdapterType arg0) {
        LOG.info("supplyDefaultProperties()");
        Map<String, String> properties = new HashMap<>();
        propertiesProvider.supplyDefault(properties);
        return properties;
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.IDataSourceAdapterProvider#
     * writeDefaultConfiguration(de.ingmbh.sphinx.online.common.vo.AdapterType,
     * java.io.OutputStream)
     */
    @Override
    public void writeDefaultConfiguration(AdapterType arg0, OutputStream arg1) throws Exception {

    }

}
