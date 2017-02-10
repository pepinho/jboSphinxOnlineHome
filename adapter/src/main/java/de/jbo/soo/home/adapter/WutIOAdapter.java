//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: WutIOAdapter.java
// Created: 03.10.2016 - 15:15:31
//
package de.jbo.soo.home.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter;
import de.ingmbh.sphinx.online.common.vo.Value;
import de.ingmbh.sphinx.online.common.vo.ValueDefinition2;
import de.jbo.soo.home.adapter.properties.PropertiesProvider;
import de.jbo.soo.home.data.WutIOInstance;

/**
 * @author Josef Baro (jbo) <br>
 * @version 03.10.2016 jbo - created <br>
 */
public class WutIOAdapter extends DataSourceAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(WutIOAdapter.class);

    /**
     * Reserved field name for adapter discovery via class path. Holds the
     * adapter type name.
     */
    public static final String ADAPTER_TYPE_NAME = "WUT-IO";

    /**
     * Reserved field name for adapter discovery via class path. Holds the
     * adapter type description.
     */
    public static final String ADAPTER_TYPE_DESCRIPTION = "Adapter connecting to WUT-IO devices via http";

    private static final long REFRESH_TIMER = 1000;

    private PropertiesProvider propertiesProvider = new PropertiesProvider();

    private List<WutIOInstance> wuts = new ArrayList<>();

    private volatile boolean isTimerActive = false;

    private Timer timerWutRefresh = new Timer("WUT refresh timer", true);

    private TimerTask timerWutRefreshTaks = null;

    private void closeWUTs() {
        LOG.info("Closed WUTs...");
        synchronized (wuts) {
            for (WutIOInstance wut : wuts) {
                wut.close();
            }
            wuts.clear();
        }
    }

    private void initWUTs() {
        synchronized (wuts) {
            LOG.info("Initializing WUTs...");
            wuts.clear();
            for (int wutIndex = 1; wutIndex <= PropertiesProvider.SUPPORTED_WUT_COUNT; wutIndex++) {
                LOG.info("Initializing WUT " + wutIndex);
                String hostAddress = propertiesProvider.getHostAddressValue(getProperties(), wutIndex);
                if ((hostAddress != null) && !hostAddress.isEmpty()) {
                    LOG.info("Connecting WUT + " + wutIndex + " to " + hostAddress);
                    WutIOInstance wut = new WutIOInstance(hostAddress, wutIndex);
                    dumpProperties("wut" + wutIndex, getProperties());
                    wut.initDataPoints(propertiesProvider, getProperties(), getAdapterSpec());
                    wut.connect();
                    wuts.add(wut);
                } else {
                    LOG.info("No address property set for WUT " + wutIndex);
                }
            }
        }
    }

    private void stopTimer() {
        if (isTimerActive) {
            LOG.info("Stopping timer to refresh WUTs...");
            isTimerActive = false;
            timerWutRefresh.cancel();
            timerWutRefresh.purge();
            timerWutRefreshTaks.cancel();
        }
    }

    private void startTimer() {
        if (!isTimerActive) {
            LOG.info("Starting timer to refresh WUTs...");
            isTimerActive = true;
            timerWutRefreshTaks = new TimerTask() {
                @Override
                public void run() {
                    refreshWUTs();
                }
            };
            timerWutRefresh.schedule(timerWutRefreshTaks, REFRESH_TIMER, REFRESH_TIMER);
        }
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#
     * afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        LOG.info("afterPropertiesSet()");
        stopTimer();
        closeWUTs();
        initWUTs();
        startTimer();
    }

    private void refreshWUTs() {
        synchronized (wuts) {
            for (WutIOInstance wut : wuts) {
                if (wut.isConnected()) {
                    LOG.debug("Refreshing wut...");
                    Collection<Value> changedValues = wut.refreshValues();
                    if (!changedValues.isEmpty()) {
                        LOG.debug("wut values changed on refresh...");
                        informValuesChanged(changedValues, true);
                    }
                }
            }
        }
    }

    private void dumpProperties(String prefix, Map<String, String> properties) {
        StringBuilder buffer = new StringBuilder();
        buffer.append('\n');
        buffer.append("Properties for " + prefix);
        buffer.append('\n');
        for (String property : properties.keySet()) {
            if (property.contains(prefix)) {
                String value = properties.get(property);
                if (!value.trim().isEmpty()) {
                    buffer.append(property);
                    buffer.append('=');
                    buffer.append(value);
                    buffer.append('\n');
                }
            }
        }
        LOG.info(buffer.toString());
    }

    private Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        Configuration configuration = dataSourceAdapterContext.getAdapterSpecConfiguration();
        Iterator<String> keys = configuration.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            properties.put(key, configuration.getProperty(key).toString());
        }

        return properties;
    }

    /*
     * @see
     * de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#doDisposeAdapter
     * ()
     */
    @Override
    protected void doDisposeAdapter() throws Throwable {
        stopTimer();
        closeWUTs();
    }

    /*
     * @see
     * de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#getAvailableData
     * ()
     */
    @Override
    public Collection<ValueDefinition2> getAvailableData() {
        LOG.info("getAvailableData()");
        List<ValueDefinition2> definitions = new ArrayList<>();
        synchronized (wuts) {
            for (WutIOInstance wut : wuts) {
                definitions.addAll(wut.getValueDefinitions());
            }
        }
        return definitions;
    }

    /*
     * @see
     * de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#getValues(java.
     * util.Collection)
     */
    @Override
    public Collection<Value> getValues(Collection<String> ids) {
        LOG.info("getValues()");
        List<Value> definitions = new ArrayList<>();
        synchronized (wuts) {
            for (WutIOInstance wut : wuts) {
                definitions.addAll(wut.getValues(ids));
            }
        }
        return definitions;
    }

    /*
     * @see
     * de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#isPollingNeeded(
     * )
     */
    @Override
    public boolean isPollingNeeded() {
        LOG.info("isPollingNeeded()");
        return false;
    }

    /*
     * @see
     * de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#setValues(java.
     * util.Collection)
     */
    @Override
    public boolean setValues(Collection<Value> values) {
        LOG.info("setValues()");
        synchronized (wuts) {
            for (WutIOInstance wut : wuts) {
                wut.setValues(values);
            }
        }
        informValuesChanged(values, true);
        return true;
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#
     * supplyDefaultProperties(java.util.Map)
     */
    @Override
    public void supplyDefaultProperties(Map<String, String> properties) {
        LOG.info("supplyDefaultProperties()");
        propertiesProvider.supplyDefault(properties);
    }

}
