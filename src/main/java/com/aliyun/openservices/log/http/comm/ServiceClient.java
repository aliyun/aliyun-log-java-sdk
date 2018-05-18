/**
 * Copyright (C) Alibaba Cloud Computing
 * All rights reserved.
 * 
 * 版权所有 （C）阿里云计算有限公司
 */
package com.aliyun.openservices.log.http.comm;

import static com.aliyun.openservices.log.http.utils.CodingUtils.assertParameterNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.aliyun.openservices.log.http.utils.HttpUtil;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.client.ClientException;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.client.ServiceException;

/**
 * The client that accesses Aliyun services.
 * @author xiaoming.yin
 *
 */
public abstract class ServiceClient {

    /**
     * A wrapper class to HttpMessage.
     * It contains the data to create HttpRequestBase,
     * and it is easy for testing to verify the built data such as URL, content.
     * @author xiaoming.yin
     *
     */
    public static class Request extends HttpMesssage{
        private String uri;
        private HttpMethod method;

        public Request(){

        }

        public String getUri(){
            return this.uri;
        }

        public void setUrl(String uri){
            this.uri = uri;
        }

        /**
         * @return the method
         */
        public HttpMethod getMethod() {
            return method;
        }

        /**
         * @param method the method to set
         */
        public void setMethod(HttpMethod method) {
            this.method = method;
        }
    }

    private static final int DEFAULT_MARK_LIMIT = 1024 * 4;

    protected ClientConfiguration config;

    protected ServiceClient(ClientConfiguration config){
        this.config = config;
    }
    
    /**
     * Returns response from the service.
     * @param request
     *          Request message.
     * @param charset
     *          encode charset.
     */
    public ResponseMessage sendRequest(RequestMessage request, String charset)
            throws ServiceException, ClientException{
        assertParameterNotNull(request, "request");
        assertParameterNotNull(charset, "charset");

        try{
            return sendRequestImpl(request, charset);
        } finally {
            // Close the request stream as well after the request is complete.
            try {
                request.close();
            } catch (IOException e) { }
        }
    }

    private ResponseMessage sendRequestImpl(RequestMessage request,
            String charset) throws ClientException, ServiceException {
        ResponseMessage response = null;

        InputStream content = request.getContent();

        if (content != null && content.markSupported()) {
            content.mark(DEFAULT_MARK_LIMIT);
        }

        try {
            Request httpRequest = buildRequest(request, charset);
            // post process request
            response = sendRequestCore(httpRequest, charset);
            return response;
        } catch (ServiceException ex) {
            // Notice that the response should not be closed in the
            // finally block because if the request is successful,
            // the response should be returned to the callers.
            closeResponseSilently(response);
            throw ex;
        } catch (ClientException ex) { 
            // Notice that the response should not be closed in the
            // finally block because if the request is successful,
            // the response should be returned to the callers.
            closeResponseSilently(response);
            throw ex;
        } catch (Exception ex) { 
            // Notice that the response should not be closed in the
            // finally block because if the request is successful,
            // the response should be returned to the callers.
            closeResponseSilently(response);
			throw new ClientException(ex.getMessage(), ex);
        } finally {
        }
    }

    /**
     * Implements the core logic to send requests to Aliyun services.
     * @param request
     * @param charset
     * @return response message
     * @throws Exception
     */
    protected abstract ResponseMessage sendRequestCore(Request request, String charset)
            throws Exception;

    private Request buildRequest(RequestMessage requestMessage, String charset)
            throws ClientException{
        Request request = new Request();
        request.setMethod(requestMessage.getMethod());
        request.setHeaders(requestMessage.getHeaders());
        // The header must be converted after the request is signed,
        // otherwise the signature will be incorrect.
        if (request.getHeaders() != null){
            HttpUtil.convertHeaderCharsetToIso88591(request.getHeaders());
        }

        final String delimiter = "/";
        String uri = requestMessage.getEndpoint().toString();
        if (! uri.endsWith(delimiter)
                && (requestMessage.getResourcePath() == null ||
                ! requestMessage.getResourcePath().startsWith(delimiter))){
            uri += delimiter;
        }

        if (requestMessage.getResourcePath() != null){
            uri += requestMessage.getResourcePath();
        }

        String paramString;
        try {
            paramString = HttpUtil.paramToQueryString(requestMessage.getParameters(), charset);
        } catch (UnsupportedEncodingException e) {
            // Assertion error because the caller should guarantee the charset.
            throw new AssertionError(("EncodingFailed" + e.getMessage()));
        }
        /*
         * For all non-POST requests, and any POST requests that already have a
         * payload, we put the encoded params directly in the URI, otherwise,
         * we'll put them in the POST request's payload.
         */
        boolean requestHasNoPayload = requestMessage.getContent() != null;
        boolean requestIsPost = requestMessage.getMethod() == HttpMethod.POST;
        boolean putParamsInUri = !requestIsPost || requestHasNoPayload;
        if (paramString != null && putParamsInUri) {
            uri += "?" + paramString;
        }

        request.setUrl(uri);
        if (requestIsPost && requestMessage.getContent() == null && paramString != null){
            // Put the param string to the request body if POSTing and
            // no content.
            try {
                byte[] buf = paramString.getBytes(charset);
                ByteArrayInputStream content = new ByteArrayInputStream(buf);
                request.setContent(content);
                request.setContentLength(buf.length);
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("EncodingFailed" + e.getMessage());
            }
        } else{
            request.setContent(requestMessage.getContent());
            request.setContentLength(requestMessage.getContentLength());
        }

        return request;
    }

    private void closeResponseSilently(ResponseMessage response){
        if (response != null){
            try {
                response.close();
            } catch (IOException ioe) { /* silently close the response.*/ }
        }
    }
    
}

