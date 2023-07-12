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
        String url = buildUrl();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000).setSocketTimeout(3000).build();
        httpGet.setConfig(config);
        TemporaryCredentials credentials;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            credentials = parse(httpResponse);
        } catch (Exception e) {
            throw new RuntimeException("Fail to fetch credentials", e);
        } finally {
            try {
                httpResponse.close();
            } catch (Exception e) {
            }
        }
        return credentials;
    }
}
