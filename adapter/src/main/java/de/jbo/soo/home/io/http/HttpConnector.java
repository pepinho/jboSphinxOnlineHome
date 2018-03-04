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
import org.apache.http.client.config.RequestConfig;
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

    private HttpClient httpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        return httpClientBuilder.build();
    }

    private HttpGet httpGet(String address) {
        HttpGet getRequest = new HttpGet(address);
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(5000).build();
        getRequest.setConfig(config);
        getRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml");
        getRequest.setHeader("Accept-Encoding", "gzip, deflate");
        getRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36 Edge/12.0");
        getRequest.setHeader("Accept-Charset", System.getProperty("file.encoding"));

        return getRequest;
    }

    /*
     * @see
     * de.jbo.soo.home.io.AbstractTcpConnector#connectExec(java.lang.String)
     */
    @Override
    protected boolean connectExec(String address) throws ConnectionFailedException {
        LOG.info("Connecting to: " + address);
        HttpClient httpClient = httpClient();
        HttpGet getRequest = httpGet(address);
        try {
            HttpResponse response = httpClient.execute(getRequest);
            LOG.trace("HTTP response: " + response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase());
            boolean status = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            getRequest.releaseConnection();
            LOG.info("Connection established: " + status);
            if (!status) {
                throw new IOException("HTTP response code " + response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase());
            }
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
        String content = null;
        HttpGet getRequest = httpGet(getAddress() + command);
        try {
            HttpResponse response = httpClient().execute(getRequest);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                LOG.trace("     Command sent. Reading response content...");
                content = getContent(response);
            } else {
                throw new IOException("HTTP call returned status " + status + " (" + response.getStatusLine().getReasonPhrase() + ")");
            }
        } catch (IOException e) {
            throw new SendFailedException(getAddress(), command, e);
        } finally {
            getRequest.releaseConnection();
        }
        return content;
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
