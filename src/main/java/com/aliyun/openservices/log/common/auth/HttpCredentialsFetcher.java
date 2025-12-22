package com.aliyun.openservices.log.common.auth;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.util.concurrent.TimeUnit;

public abstract class HttpCredentialsFetcher implements CredentialsFetcher {

    /**
     * Constructs the url of authorization server.
     *
     * @return the url of authorization server
     */
    public abstract String buildUrl();

    /**
     * Parses the response to get credentials.
     *
     * @param response http response.
     * @return credentials
     */
    public abstract TemporaryCredentials parse(CloseableHttpResponse response) throws Exception;

    @Override
    public TemporaryCredentials fetch() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = buildHttpRequest();

        Exception capturedException = null;
        int retryTimes = FETCH_CREDENTIALS_MAX_RETRY_TIMES;
        while (retryTimes >= 0) {
            try {
                return fetchOnce(httpClient, httpGet);
            } catch (Exception e) {
                capturedException = e;
            }
            retryTimes--;
        }
        throw new RuntimeException("Fail to fetch credentials, max retry times exceeded", capturedException);
    }

    private HttpGet buildHttpRequest() {
        HttpGet httpGet = new HttpGet(buildUrl());
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(3000, TimeUnit.MILLISECONDS)
                .setConnectionRequestTimeout(3000, TimeUnit.MILLISECONDS)
                .setResponseTimeout(3000, TimeUnit.MILLISECONDS)
                .build();
        httpGet.setConfig(config);
        return httpGet;
    }

    protected TemporaryCredentials fetchOnce(CloseableHttpClient httpClient, HttpGet httpGet) throws Exception {
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            return parse(httpResponse);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static final int FETCH_CREDENTIALS_MAX_RETRY_TIMES = 3;
}
