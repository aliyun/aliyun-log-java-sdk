/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Object's metadata. It has the user's custom metadata, as well as some
 * standard http headers sent to the server, such as Content-Length, ETag, etc.
 */
public class ObjectMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, String> headers;

    private static final String X_LOG_META_PREFIX = "x-log-meta-";

    public ObjectMetadata(Map<String, String> headers) {
        this.headers = copyCaseInsensitiveHeaders(headers);
    }

    private Map<String, String> copyCaseInsensitiveHeaders(Map<String, String> headers) {
        HashMap<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            result.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return result;
    }

    /**
     * Gets the user's custom metadata, whose prefix in http header is x-log-meta-.
     *
     * @return The user's custom metadata, may be empty if not set
     */
    public Map<String, String> getCustomHeaders() {
        return headers.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(X_LOG_META_PREFIX))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets the value of Last-Modified header, which means the last modified
     * time of the object.
     *
     * @return Object's last modified time, may be null if not set
     */
    public String getLastModified() {
        return headers.get("last-modified");
    }

    /**
     * Gets Content-Length header, which is the object content's size.
     *
     * @return The object content's size, 0 if not set
     */
    public long getContentLength() {
        return Long.parseLong(headers.get("content-length"));
    }

    /**
     * Gets Content-Type header, which is the object content's type.
     *
     * @return The object content's type, may be null if not set
     */
    public String getContentType() {
        return headers.get("content-type");
    }

    /**
     * Gets Content-MD5 header, which is the object content's MD5, base64 encoded.
     *
     * @return The object content's MD5, may be null if not set
     */
    public String getContentMD5() {
        return headers.get("content-md5");
    }

    /**
     * Gets the ETag header.
     *
     * @return ETag value, may be null if not set
     */
    public String getETag() {
        return headers.get("etag");
    }

}
