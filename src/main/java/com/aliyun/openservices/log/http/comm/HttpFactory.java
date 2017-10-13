/**
 * Copyright (C) Alibaba Cloud Computing
 * All rights reserved.
 * 
 * 版权所有 （C）阿里云计算有限公司
 */
package com.aliyun.openservices.log.http.comm;

import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.aliyun.openservices.log.http.utils.HttpHeaders;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * The factory to create HTTP-related objects.
 * @author xiaoming.yin
 *
 */
class HttpFactory {

    /**
     * Creates a HttpClient instance.
     * @param config
     *          Client configuration.
     * @return HttpClient instance.
     */
    public HttpClient createHttpClient(ClientConfiguration config){
        // Set HTTP params.
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, config.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(httpParams, config.getSocketTimeout());
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, true);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);

        // Use thread-safe connection manager.
        ThreadSafeClientConnManager connMgr = createThreadSafeClientConnManager(config);
        DefaultHttpClient httpClient = new DefaultHttpClient(connMgr, httpParams);

        /*
         * If SSL cert checking for endpoints has been explicitly disabled,
         * register a new scheme for HTTPS that won't cause self-signed certs to
         * error out.
         */
        if (System.getProperty("com.aliyun.openservices.disableCertChecking") != null) {
            Scheme sch = new Scheme("https", 443, getSSLSocketFactory());
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }
        return httpClient;
    }

    private ThreadSafeClientConnManager createThreadSafeClientConnManager(
            ClientConfiguration clientConfig){
        ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager();
        connMgr.setDefaultMaxPerRoute(clientConfig.getMaxConnections());
        connMgr.setMaxTotal(clientConfig.getMaxConnections());
        return connMgr;
    }

    /**
     * Creates a HttpRequestBase instance.
     * @param request
     *          Request message.
     * @param charset
     *          encode charset.
     * @return HttpRequestBase instance.
     */
    public HttpRequestBase createHttpRequest(ServiceClient.Request request, String charset){

        String uri = request.getUri();
        HttpMethod method = request.getMethod();
        HttpRequestBase httpRequest;
        if (method == HttpMethod.POST){
            // POST
            HttpPost postMethod = new HttpPost(uri);

            if (request.getContent() != null){
                postMethod.setEntity(new RepeatableInputStreamEntity(request));
            }

            httpRequest = postMethod;
        } else if (method == HttpMethod.PUT){
            // PUT
            HttpPut putMethod = new HttpPut(uri);

            if (request.getContent() != null){
                putMethod.setEntity(new RepeatableInputStreamEntity(request));
            }

            httpRequest = putMethod;
        } else if (method == HttpMethod.GET){
            // GET
            httpRequest = new HttpGet(uri);
        } else if (method == HttpMethod.DELETE){
            // DELETE
            httpRequest = new HttpDelete(uri);
        } else if (method == HttpMethod.HEAD){
            httpRequest = new HttpHead(uri);
        } else if (method == HttpMethod.OPTIONS){
            httpRequest = new HttpOptions(uri);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported HTTP method：%s.", request.getMethod().toString()));
        }

        configureRequestHeaders(request, charset, httpRequest);

        return httpRequest;
    }

    private void configureRequestHeaders(ServiceClient.Request request, 
            String charset, HttpRequestBase httpRequest){
        // Copy headers in the request message to the HTTP request
        for(Entry<String, String> entry : request.getHeaders().entrySet()){
            // HttpClient fills in the Content-Length,
            // and complains if add it again, so skip it as well as the Host header.
            /*
        	if (entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH) ||
                    entry.getKey().equalsIgnoreCase(HttpHeaders.HOST)){
                continue;
            }
            */
            if (entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)){
                continue;
            }

            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }

        // Set content type and encoding
        if (httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE) == null ||
                httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE).length == 0){
            httpRequest.addHeader(HttpHeaders.CONTENT_TYPE,
                    "application/x-www-form-urlencoded; " +
                            "charset=" + charset.toLowerCase());
        }
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null; 
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, null);
            SSLSocketFactory ssf =
                    new SSLSocketFactory(sslcontext,
                            SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return ssf;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

