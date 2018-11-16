/**
 * Copyright (C) Alibaba Cloud Computing
 * All rights reserved.
 * 
 * 版权所有 （C）阿里云计算有限公司
 */
package com.aliyun.openservices.log.http.comm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import com.aliyun.openservices.log.http.utils.ExceptionFactory;
import com.aliyun.openservices.log.http.utils.HttpUtil;
import com.aliyun.openservices.log.http.client.ClientConfiguration;


/**
 * The default implementation of <code>ServiceClient</code>.
 * @author xiaoming.yin
 *
 */
public class DefaultServiceClient extends ServiceClient {

    private HttpClient httpClient;

    /**
     * 构造新实例。
     */
    public DefaultServiceClient(ClientConfiguration config) {
        super(config);
        httpClient = new HttpFactory().createHttpClient(config);
    }

    @Override
    public ResponseMessage sendRequestCore(ServiceClient.Request request, String charset)
            throws IOException{
        assert request != null;
        
        // When we release connections, the connection manager leaves them
        // open so they can be reused.  We want to close out any idle
        // connections so that they don't sit around in CLOSE_WAIT.
        httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS);

        HttpRequestBase httpRequest = new HttpFactory().createHttpRequest(request, charset);

        // Execute request, make the exception to the standard WebException
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpRequest);
        } catch (IOException ex) {
            throw ExceptionFactory.createNetworkException(ex);
        }

        // Build result
        ResponseMessage result = new ResponseMessage();
        result.setUrl(request.getUri());
        if (response.getStatusLine() != null){
            result.setStatusCode(response.getStatusLine().getStatusCode());
        }
        if (response.getEntity() != null){
            result.setContent(response.getEntity().getContent());
        }
        // fill in headers
        Header[] headers = response.getAllHeaders();
        Map<String, String> resultHeaders = new HashMap<String, String>();
        for(int i = 0; i < headers.length; i++){
            Header h = headers[i];
            resultHeaders.put(h.getName(), h.getValue());
        }
        HttpUtil.convertHeaderCharsetFromIso88591(resultHeaders);
        result.setHeaders(resultHeaders);

        return result;
    }
}
