/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.openservices.log.http.utils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {
    public static final String DEFAULT_CHARSET_NAME = "utf-8";

    /**
     * Encode a URL segment with special chars replaced.
     */
    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }

        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }

    public static String percentEncode(String value) {
        try {
            return value != null ? URLEncoder.encode(value, DEFAULT_CHARSET_NAME)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~") : null;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * A null-safe trim method. If the input string is null, returns "";
     * otherwise returns a trimmed version of the input.
     */
    public static String trim(String value) {
        return value != null ? value.trim() : "";
    }

    /**
     * Encode request parameters to URL segment.
     */
    public static String paramToQueryString(Map<String, String> params, String charset) {

        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String value = p.getValue();

            if (!first) {
                paramString.append("&");
            }

            // Urlencode each request parameter
            paramString.append(urlEncode(key, charset));
            if (value != null) {
                paramString.append("=").append(urlEncode(value, charset));
            }

            first = false;
        }

        return paramString.toString();
    }

    private static final String ISO_8859_1_CHARSET = "iso-8859-1";
    private static final String UTF8_CHARSET = "utf-8";

    // To fix the bug that the header value could not be unicode chars.
    // Because HTTP headers are encoded in iso-8859-1,
    // we need to convert the utf-8(java encoding) strings to iso-8859-1 ones.
    public static void convertHeaderCharsetFromIso88591(Map<String, String> headers) {
        convertHeaderCharset(headers, ISO_8859_1_CHARSET, UTF8_CHARSET);
    }

    // For response, convert from iso-8859-1 to utf-8.
    public static void convertHeaderCharsetToIso88591(Map<String, String> headers) {
        convertHeaderCharset(headers, UTF8_CHARSET, ISO_8859_1_CHARSET);
    }

    private static void convertHeaderCharset(Map<String, String> headers, String fromCharset, String toCharset) {

        for (Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getValue() == null) {
                continue;
            }

            try {
                header.setValue(new String(header.getValue().getBytes(fromCharset), toCharset));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Invalid charset name: " + e.getMessage(), e);
            }
        }
    }
}
