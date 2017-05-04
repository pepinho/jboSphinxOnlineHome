//
// Copyright (c) 2016 by jbo - Josef Baro
//
// Project: adapter
// File: HttpConnector.java
// Created: 05.11.2016 - 09:24:37
//
package de.jbo.soo.home.io.http;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jbo.soo.home.io.AbstractTcpConnector;
import de.jbo.soo.home.io.ConnectionFailedException;
import de.jbo.soo.home.io.SendFailedException;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.11.2016 jbo - created <br>
 */
public class HttpConnector extends AbstractTcpConnector {
    private static final Logger LOG = LoggerFactory.getLogger(HttpConnector.class);

    private HttpClient httpClient = null;

    /*
     * @see
     * de.jbo.soo.home.io.AbstractTcpConnector#connectExec(java.lang.String)
     */
    @Override
    protected boolean connectExec(String address) throws ConnectionFailedException {
        LOG.info("Connecting to: " + address);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClient = httpClientBuilder.build();
        HttpGet getRequest = new HttpGet(address);
        try {
            HttpResponse response = httpClient.execute(getRequest);
            boolean status = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            getRequest.releaseConnection();
            LOG.info("Connection established: " + status);
            return status;
        } catch (IOException e) {
            throw new ConnectionFailedException(address, e);
        }
    }

    /*
     * @see de.jbo.soo.home.io.AbstractTcpConnector#execClose()
     */
    @Override
    protected boolean execClose() {
        LOG.info("Closing connection...");
        httpClient = null;
        LOG.info("Connection closed.");
        return true;
    }

    /*
     * @see
     * de.jbo.soo.home.io.AbstractTcpConnector#execSendCommand(java.lang.String)
     */
    @Override
    protected String execSendCommand(String command) throws SendFailedException {
        LOG.trace("      execSendCommand: " + getAddress() + command);
        HttpGet getRequest = new HttpGet(getAddress() + command);
        try {
            HttpResponse response = httpClient.execute(getRequest);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                LOG.trace("     Command sent. Reading response content...");
                String content = getContent(response);
                getRequest.releaseConnection();
                return content;
            } else {
                throw new IOException("HTTP call returned status " + status + " (" + response.getStatusLine().getReasonPhrase() + ")");
            }
        } catch (IOException e) {
            throw new SendFailedException(getAddress(), command, e);
        }
    }

    private String getContent(HttpResponse response) throws UnsupportedOperationException, IOException {
        StringBuffer stringBuffer = new StringBuffer();
        InputStream content = response.getEntity().getContent();
        byte[] buf = new byte[1024];
        int read = content.read(buf);
        while (read > 0) {
            stringBuffer.append(new String(buf, 0, read));
            read = content.read(buf);
        }
        content.close();
        String contentRead = stringBuffer.toString();
        LOG.trace("     Response content: " + contentRead);
        return contentRead;
    }

}
