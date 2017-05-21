//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: servlet
// File: BaseController.java
// Created: 16.07.2016 - 11:30:02
//
package de.jbo.soo.home.connection.wut.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.jbo.soo.home.connection.wut.data.DataStore;
import de.jbo.soo.home.connection.wut.io.IOProcessor;

/**
 * @author Josef Baro (jbo) <br>
 * @version 16.07.2016 jbo - created <br>
 */
@Controller
public class BaseController {

    static final int DATA_STORE_SIZE = 12;

    private static final String DATASTORE_FILENAME = "wutServletDataStore.properties";

    private static final String VARIABLE_INDEX = "index";

    private static final String MODEL_REQUEST = "request";

    private static final String MODEL_RESPONSE = "response";

    private static String lastRequest = "";

    private static String lastResponse = "";

    static final String VIEW_INDEX = "index";

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);

    private DataStore dataStore;

    private String dataStoreFileName;

    private static synchronized void assertPassword(String password) throws IllegalAccessException {
        if (!password.equals(IOProcessor.PASSWORD)) {
            throw new IllegalAccessException("Wrong password!");
        }
    }

    synchronized void clearDataStoreFile() {
        validateDataStore();
        new File(dataStoreFileName).delete();
    }

    synchronized void validateDataStore() {
        if (dataStore == null) {
            logger.info("Initializing datastore...");
            dataStoreFileName = createDataStoreFilename();
            dataStore = new DataStore(DATA_STORE_SIZE);
            logger.info("Initializing datastore '" + dataStoreFileName + "' finished.");
            if (!new File(dataStoreFileName).exists()) {
                saveDataStore();
            }
        }
        loadDataStore();
    }

    private String createDataStoreFilename() {
        String prefix = DATASTORE_FILENAME.substring(0, DATASTORE_FILENAME.indexOf('.'));
        String suffix = DATASTORE_FILENAME.substring(DATASTORE_FILENAME.indexOf('.'));
        String middle = "";
        try {
            ServletUriComponentsBuilder myUri = ServletUriComponentsBuilder.fromCurrentServletMapping();
            if (myUri != null) {
                String uri = myUri.toUriString();
                middle = "-" + uri.substring(uri.indexOf("wut-servlet"));
            }
        } catch (IllegalStateException e) {
            // ntbd
        }

        return prefix + middle + suffix;
    }

    private synchronized void loadDataStore() {
        FileInputStream fis = null;
        try {
            logger.info("Loading datastore from: " + dataStoreFileName + "...");
            fis = new FileInputStream(dataStoreFileName);
            if (!dataStore.load(fis)) {
                throw new IOException("dataStore.load() returned 'false'");
            }
        } catch (Exception e) {
            logger.error("Loading datastore failed.", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ntbd
                }
            }
        }
    }

    synchronized void saveDataStore() {
        FileOutputStream fos = null;
        try {
            logger.info("Saving datastore to: " + dataStoreFileName + "...");
            fos = new FileOutputStream(dataStoreFileName);
            if (!dataStore.save(fos)) {
                throw new IOException("dataStore.save() returned 'false'");
            }
        } catch (Exception e) {
            logger.error("Saving datastore failed.", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ntbd
                }
            }
        }
    }

    /**
     * Creates the index-page with the current data overview.
     *
     * @param model
     *            The internal model.
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap model) {
        validateDataStore();

        for (int i = 0; i < dataStore.size(); i++) {
            model.addAttribute(IOProcessor.ATTRIBUTE_PREFIX_OUTPUT + i, dataStore.getOutput(i));
            model.addAttribute(IOProcessor.ATTRIBUTE_PREFIX_INPUT + i, dataStore.getInput(i));
            model.addAttribute(IOProcessor.ATTRIBUTE_PREFIX_COUNTER + i, dataStore.getCounter(i));
        }

        model.addAttribute(MODEL_RESPONSE, lastResponse);
        model.addAttribute(MODEL_REQUEST, lastRequest);

        // Spring uses InternalResourceViewResolver and return back index.jsp
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/output", method = RequestMethod.GET)
    @ResponseBody()
    public String outputs(@RequestParam(value = IOProcessor.PARAM_PW, required = true) String pw, ModelMap model) throws IllegalAccessException {
        synchronized (this) {
            assertPassword(pw);
            validateDataStore();
            logger.debug("outputs()");
            StringBuffer response = IOProcessor.createResponseOutputs(dataStore);
            lastResponse = response.toString();
            lastRequest = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_OUTPUT, pw);
            model.addAttribute(MODEL_RESPONSE, lastResponse);
            model.addAttribute(MODEL_REQUEST, lastRequest);
            return lastResponse;
        }
    }

    @RequestMapping(value = "/input", method = RequestMethod.GET)
    @ResponseBody()
    public String inputs(@RequestParam(value = IOProcessor.PARAM_PW, required = true) String pw, ModelMap model) throws IllegalAccessException {
        synchronized (this) {
            assertPassword(pw);
            validateDataStore();
            logger.debug("inputs()");
            StringBuffer response = IOProcessor.createResponseInputs(dataStore);
            lastResponse = response.toString();
            lastRequest = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_INPUT, pw);
            model.addAttribute(MODEL_RESPONSE, lastResponse);
            model.addAttribute(MODEL_REQUEST, lastRequest);
            return lastResponse;
        }
    }

    @RequestMapping(value = "/counter", method = RequestMethod.GET)
    @ResponseBody()
    public String counters(@RequestParam(value = IOProcessor.PARAM_PW, required = true) String pw, ModelMap model) throws IllegalAccessException {
        synchronized (this) {
            assertPassword(pw);
            validateDataStore();
            logger.debug("counters()");
            StringBuffer response = IOProcessor.createResponseCounters(dataStore);
            lastResponse = response.toString();
            lastRequest = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_COUNTER, pw);
            model.addAttribute(MODEL_RESPONSE, lastResponse);
            model.addAttribute(MODEL_REQUEST, lastRequest);
            return lastResponse;
        }
    }

    @RequestMapping(value = "/counterclear", method = RequestMethod.GET)
    public String counterclear(@RequestParam(value = IOProcessor.PARAM_PW, required = true) String pw, ModelMap model) throws IllegalAccessException {
        synchronized (this) {
            assertPassword(pw);
            validateDataStore();
            logger.debug("counterclear()");
            IOProcessor.processCounterClear(dataStore);
            saveDataStore();
            lastResponse = "";
            lastRequest = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_COUNTERCLEAR, pw);
            index(model);
        }
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/outputaccess{" + VARIABLE_INDEX + "}", method = RequestMethod.GET)
    public String outputaccess(@RequestParam(value = IOProcessor.PARAM_PW, required = true) String pw, @PathVariable(VARIABLE_INDEX) int index, @RequestParam(value = IOProcessor.PARAM_STATE, required = true) String state, ModelMap model)
            throws IllegalAccessException {
        synchronized (this) {
            assertPassword(pw);
            validateDataStore();
            logger.debug("outputaccess(index " + index + ", " + IOProcessor.PARAM_STATE + " " + state + ")");
            IOProcessor.processOutputAccess(index, state, dataStore);
            saveDataStore();
            lastResponse = "";
            lastRequest = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_OUTPUTACCESS + index, pw, IOProcessor.PARAM_STATE, state);
            index(model);
        }
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/counterclear{" + VARIABLE_INDEX + "}", method = RequestMethod.GET)
    public String counterclear(@RequestParam(value = IOProcessor.PARAM_PW, required = true) String pw, @PathVariable(VARIABLE_INDEX) int index, ModelMap model) throws IllegalAccessException {
        synchronized (this) {
            assertPassword(pw);
            validateDataStore();
            logger.debug("counterclear(index " + index + ")");
            IOProcessor.processCounterClear(index, dataStore);
            saveDataStore();
            lastResponse = "";
            lastRequest = IOProcessor.createRequest(IOProcessor.ATTRIBUTE_PREFIX_COUNTERCLEAR + index, pw);
            index(model);
        }
        return VIEW_INDEX;
    }

    DataStore getDataStore() {
        return dataStore;
    }
}