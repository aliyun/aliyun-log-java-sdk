package com.aliyun.openservices.log.common.auth;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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
    public abstract TemporaryCredentials parse(HttpResponse response) throws Exception;

    @Override
    public TemporaryCredentials fetch() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
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
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000).setSocketTimeout(3000).build();
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
