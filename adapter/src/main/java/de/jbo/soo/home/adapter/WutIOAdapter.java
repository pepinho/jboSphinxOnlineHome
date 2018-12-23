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
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter6;
import de.ingmbh.sphinx.online.common.vo.DataType;
import de.ingmbh.sphinx.online.common.vo.Value;
import de.ingmbh.sphinx.online.common.vo.ValueDefinition2;
import de.ingmbh.sphinx.online.common.vo.ValueType;
import de.jbo.soo.home.Version;
import de.jbo.soo.home.adapter.properties.PropertiesProvider;
import de.jbo.soo.home.data.WutIOInstance;

/**
 * @author Josef Baro (jbo) <br>
 * @version 03.10.2016 jbo - created <br>
 */
public class WutIOAdapter extends DataSourceAdapter6 {
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

    private static Version VERSION = Version.getVersionFromBuild(WutIOAdapter.class);

    private static final long REFRESH_TIMER = 1000;

    private PropertiesProvider propertiesProvider = null;

    private List<WutIOInstance> wuts = new ArrayList<>();

    private volatile boolean isTimerActive = false;

    private Timer timerWutRefresh = new Timer("WUT refresh timer", true);

    private TimerTask timerWutRefreshTask = null;

    private ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(PropertiesProvider.SUPPORTED_WUT_COUNT);

    private boolean useSeparateRefreshThreads = false;

    private boolean isStartup = true;

    private Value versionValue = null;

    public static Version getVersion() {
        LOG.info("Init version information...");
        VERSION = Version.getVersionFromBuild(WutIOAdapter.class);
        LOG.info("Init version information finished: {}", VERSION.toString());
        return VERSION;
    }

    public WutIOAdapter(PropertiesProvider propertiesProvider) {
        super();
        this.propertiesProvider = propertiesProvider;
    }

    private void closeWUTs() {
        LOG.info("Closed WUTs...");
        synchronized (wuts) {
            for (WutIOInstance wut : wuts) {
                wut.close();
            }
            wuts.clear();
        }
    }

    private WutIOInstance initWut(int wutIndex) {
        LOG.info("Initializing WUT " + wutIndex);
        String hostAddress = propertiesProvider.getHostAddressValue(getProperties(), wutIndex);
        if ((hostAddress != null) && !hostAddress.isEmpty()) {
            LOG.info("Providing WUT + " + wutIndex + " datapoints...");
            WutIOInstance wut = new WutIOInstance(hostAddress, wutIndex);
            dumpProperties("wut" + wutIndex, getProperties());
            wut.initDataPoints(propertiesProvider, getProperties(), getAdapterSpec());
            LOG.info("WUT " + wutIndex + " successfully initialized.");
            return wut;
        }
        return null;
    }

    private void initWUTs() {
        synchronized (wuts) {
            LOG.info("Initializing WUTs...");
            wuts.clear();
            for (int wutIndex = 1; wutIndex <= PropertiesProvider.SUPPORTED_WUT_COUNT; wutIndex++) {
                WutIOInstance wut = initWut(wutIndex);
                if (wut != null) {
                    wuts.add(wut);
                }
            }
        }
    }

    private boolean isConnectionsAvailable() {
        boolean wutAvailable = false;
        synchronized (wuts) {
            wutAvailable = !wuts.isEmpty();
        }
        return wutAvailable;
    }

    private void stopTimer() {
        if (isTimerActive) {
            LOG.info("Stopping timer to refresh WUTs...");
            isTimerActive = false;
            timerWutRefresh.cancel();
            timerWutRefresh.purge();
            timerWutRefreshTask.cancel();
        }
    }

    private void startTimer() {
        if (!isTimerActive) {
            LOG.info("Starting timer to refresh WUTs...");
            isTimerActive = true;
            timerWutRefreshTask = new TimerTask() {
                @Override
                public void run() {
                    refreshWUTs();
                }
            };
            timerWutRefresh.schedule(timerWutRefreshTask, REFRESH_TIMER, REFRESH_TIMER);
        }
    }

    /*
     * @see de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#
     * afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        synchronized (wuts) {
            LOG.info("afterPropertiesSet()");
            useSeparateRefreshThreads = propertiesProvider.isUseSeparateRefreshThreads(getProperties());
            stopTimer();
            closeWUTs();
            initWUTs();
        }
    }

    private void refreshWUT(WutIOInstance wut) {
        if (wut.isConnected()) {
            Runnable schedule = new Runnable() {
                @Override
                public void run() {
                    Collection<Value> changedValues = wut.refreshValues();
                    if (!changedValues.isEmpty()) {
                        informValuesChanged(changedValues, true);
                    }
                }
            };
            if (useSeparateRefreshThreads) {
                scheduler.execute(schedule);
            } else {
                schedule.run();
            }
        }
    }

    private void refreshWUTs() {
        synchronized (wuts) {
            LOG.trace("--> refreshWUTsU()");
            for (WutIOInstance wut : wuts) {
                refreshWUT(wut);
            }
            LOG.trace("<-- refreshWUTsU()");
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
        LOG.info("--> getAvailableData()");
        List<ValueDefinition2> definitions = new ArrayList<>();
        synchronized (wuts) {
            for (WutIOInstance wut : wuts) {
                definitions.addAll(wut.getValueDefinitions());
            }
        }
        initVersionData(definitions);
        LOG.info("<-- getAvailableData()");
        return definitions;
    }

    private void initVersionData(List<ValueDefinition2> definitions) {
        String versionId = "wut.adapter.version";
        ValueDefinition2 version = new ValueDefinition2(versionId, getAdapterSpec(), versionId);
        version.setDataType(DataType.STRING);
        version.setDefaultValue(WutIOAdapter.getVersion());
        version.setReadable(true);
        version.setWritable(false);
        version.setValueType(ValueType.VALUE);
        version.setNativeObjectType("version");
        definitions.add(version);

        versionValue = new Value();
        versionValue.setTimeOfValue(System.currentTimeMillis());
        versionValue.setTimeOfChange(System.currentTimeMillis());
        versionValue.setId(versionId);
        versionValue.setDataType(version.getDataType());
        versionValue.setStatus(Value.STATUS_GOOD);
        versionValue.setValue(WutIOAdapter.getVersion().toString());
    }

    /*
     * @see
     * de.ingmbh.sphinx.online.adapter.common.DataSourceAdapter#getValues(java.
     * util.Collection)
     */
    @Override
    public Collection<Value> getValues(Collection<String> ids) {
        LOG.debug("--> getValues()");
        if (!isTimerActive && !isStartup) {
            if (isConnectionsAvailable()) {
                LOG.info("Initializing 1st connections to WUTs...");
                for (WutIOInstance wut : wuts) {
                    if (!wut.isConnected()) {
                        wut.connect();
                    }
                }
                refreshWUTs();
                LOG.info("WUTs connected. Starting timer...");
                startTimer();
            }
        }

        List<Value> values = new ArrayList<>();
        values.add(versionValue);
        if (!isStartup) {
            synchronized (wuts) {
                for (String id : ids) {
                    for (WutIOInstance wut : wuts) {
                        if (wut.isAvailable(id)) {
                            values.add(wut.getValue(id));
                            break;
                        }
                    }
                }
            }
        } else {
            LOG.info("Startup phase. Returning empty values. Connection will be established with the next call...");
            isStartup = false;
        }
        LOG.debug("<-- getValues()");
        return values;
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
        synchronized (wuts) {
            LOG.debug("--> setValues()");
            List<Value> valuesSet = new ArrayList<>();
            for (Value value : values) {
                for (WutIOInstance wut : wuts) {
                    if (wut.isAvailable(value)) {
                        Value newValue = wut.setValue(value);
                        if (newValue != null) {
                            valuesSet.add(newValue);
                        }
                        break;
                    }
                }
            }
            informValuesChanged(valuesSet, true);
            LOG.debug("<-- setValues()");
        }
        return true;
    }

}
