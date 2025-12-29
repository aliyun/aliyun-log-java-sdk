/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ObjectMetadata;
import com.aliyun.openservices.log.http.comm.ResponseMessage;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * The response of the get_object API from log.
 * The user is responsible for closing this response when done reading the content.
 */
public class GetObjectResponse extends Response implements Closeable {
    private static final long serialVersionUID = 1L;
    private ResponseMessage response;
    private ObjectMetadata objectMetadata;

    /**
     * Construct the response with http headers, body InputStream, and ResponseMessage
     *
     * @param headers http headers
     * @param response the ResponseMessage (will not be closed automatically)
     */
    public GetObjectResponse(Map<String, String> headers, ResponseMessage response) {
        super(headers);
        this.response = response;
        this.objectMetadata = new ObjectMetadata(headers);
    }

    /**
     * Get the object metadata.
     *
     * @return ObjectMetadata instance
     */
    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    /**
     * Get the object content as InputStream.
     *
     * @return object content as InputStream
     */
    public InputStream getContent() {
        return response != null ? response.getContent() : null;
    }

    /**
     * Get the underlying ResponseMessage.
     *
     * @return the ResponseMessage
     */
    public ResponseMessage getResponse() {
        return response;
    }

    /**
     * Set the underlying ResponseMessage.
     *
     * @param response the ResponseMessage
     */
    public void setResponse(ResponseMessage response) {
        this.response = response;
    }

    /**
     * Closes the InputStream. The HTTP connection will remain open until forcedClose() is called.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (response != null) {
            response.close();
        }
    }
}

