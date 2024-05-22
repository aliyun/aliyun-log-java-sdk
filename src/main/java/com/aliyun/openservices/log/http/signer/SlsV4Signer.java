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
package com.aliyun.openservices.log.http.signer;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.auth.Credentials;
import com.aliyun.openservices.log.common.auth.CredentialsProvider;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.http.utils.BinaryUtil;
import com.aliyun.openservices.log.http.utils.HttpUtil;
import com.aliyun.openservices.log.util.Args;
import org.apache.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SlsV4Signer extends SlsSignerBase implements SlsSigner {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final Charset CHARSET_UTF_8 = Charset.forName(DEFAULT_ENCODING);

    private static final List<String> DEFAULT_SIGNED_HEADERS = Arrays.asList(
            HttpHeaders.CONTENT_TYPE.toLowerCase(),
            HttpHeaders.HOST.toLowerCase());

    /* Signature method. */
    private static final String ALGORITHM = "HmacSHA256";

    private static final Object LOCK = new Object();

    /* Prototype of the Mac instance. */
    private static Mac macInstance;

    // ISO 8601 format
    private static final String ISO8601_DATETIME_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    private static final String SEPARATOR_BACKSLASH = "/";

    private static final String SLS4_HMAC_SHA256 = "SLS4-HMAC-SHA256";
    private static final String SLS_PRODUCT_NAME = "sls";
    private static final String TERMINATOR = "aliyun_v4_request";
    private static final String SECRET_KEY_PREFIX = "aliyun_v4";
    private static final String NEW_LINE = "\n";

    private final String region;

    public SlsV4Signer(CredentialsProvider credentialsProvider, String region) {
        super(credentialsProvider);
        Args.check(region != null && !region.isEmpty(), "region must not be empty for v4 signature.");
        this.region = region;
    }

    private static DateFormat getIso8601DateTimeFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATETIME_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df;
    }

    private String getDateTime(Date now) {
        return getIso8601DateTimeFormat().format(now);
    }

    private String buildCanonicalRequest(String method,
                                         String resourcePath,
                                         Map<String, String> parameters,
                                         String canonicalHeaders,
                                         String signedHeaders,
                                         String hashedPayLoad) {
        StringBuilder canonicalString = new StringBuilder();

        //http method + "\n"
        canonicalString.append(method).append(NEW_LINE);

        //Canonical URI + "\n"
        canonicalString.append(resourcePath).append(NEW_LINE);

        //Canonical Query String + "\n" +
        if (parameters != null) {
            TreeMap<String, String> orderMap = new TreeMap<String, String>(parameters);
            String separator = "";
            for (Map.Entry<String, String> param : orderMap.entrySet()) {
                canonicalString.append(separator).append(param.getKey());
                String value = param.getValue();
                if (value != null && !value.isEmpty()) {
                    canonicalString.append("=").append(HttpUtil.percentEncode(value));
                }
                separator = "&";
            }
        }
        canonicalString.append(NEW_LINE);

        //Canonical Headers + "\n" +
        canonicalString.append(canonicalHeaders).append(NEW_LINE);

        //Signed Headers + "\n" +
        canonicalString.append(signedHeaders).append(NEW_LINE);

        //Hashed PayLoad
        canonicalString.append(hashedPayLoad);
        return canonicalString.toString();
    }

    private String buildScope(String date) {
        return date + SEPARATOR_BACKSLASH +
                region + SEPARATOR_BACKSLASH +
                SLS_PRODUCT_NAME + SEPARATOR_BACKSLASH +
                TERMINATOR;
    }

    private String buildStringToSign(String canonicalString, String dateTime, String scope) {
        return SLS4_HMAC_SHA256 + NEW_LINE +
                dateTime + NEW_LINE +
                scope + NEW_LINE +
                BinaryUtil.toHex(BinaryUtil.calculateSha256(canonicalString.getBytes(CHARSET_UTF_8)));
    }

    private static Mac getMacInstance() {
        try {
            // Because Mac.getInstance(String) calls a synchronized method, it
            // could block on
            // invoked concurrently, so use prototype pattern to improve perf.
            if (macInstance == null) {
                synchronized (LOCK) {
                    if (macInstance == null) {
                        macInstance = Mac.getInstance(ALGORITHM);
                    }
                }
            }

            Mac mac;
            try {
                mac = (Mac) macInstance.clone();
            } catch (CloneNotSupportedException e) {
                // If it is not clonable, create a new one.
                mac = Mac.getInstance(ALGORITHM);
            }
            return mac;
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unsupported algorithm: " + ALGORITHM, ex);
        }
    }

    private static byte[] buildSigningKey(HmacSHA256Hasher hasher, String secretAccessKey, String region, String date) {
        byte[] signingSecret = (SECRET_KEY_PREFIX + secretAccessKey).getBytes(CHARSET_UTF_8);
        byte[] signingDate = hasher.hash(signingSecret, date.getBytes(CHARSET_UTF_8));
        byte[] signingRegion = hasher.hash(signingDate, region.getBytes(CHARSET_UTF_8));
        byte[] signingService = hasher.hash(signingRegion, SlsV4Signer.SLS_PRODUCT_NAME.getBytes(CHARSET_UTF_8));
        return hasher.hash(signingService, TERMINATOR.getBytes(CHARSET_UTF_8));
    }

    private static String buildAuthorization(String accessKeyId, String signature, String scope) {
        String credential = "Credential=" + accessKeyId + SEPARATOR_BACKSLASH + scope;
        String sign = ",Signature=" + signature;
        return SLS4_HMAC_SHA256 + " " + credential + sign;
    }

    @Override
    public void sign(HttpMethod httpMethod,
                     Map<String, String> headers,
                     String resourceUri,
                     Map<String, String> urlParams,
                     byte[] body) {
        Date now = new Date();
        String dateTime = getDateTime(now);
        headers.put(Consts.CONST_AUTHORIZATION, signRequest(headers, httpMethod, resourceUri, urlParams, body, dateTime));
    }

    public String signRequest(Map<String, String> headers, HttpMethod httpMethod,
                              String resourceUri, Map<String, String> urlParams,
                              byte[] body, String dateTime) {
        Credentials credentials = credentialsProvider.getCredentials();
        addHeaderSecurityToken(credentials.getSecurityToken(), headers);

        String contentSha256;
        if (body != null && body.length > 0) {
            headers.put(Consts.CONST_CONTENT_LENGTH, String.valueOf(body.length));
            contentSha256 = BinaryUtil.toHex(BinaryUtil.calculateSha256(body));
        } else {
            headers.put(Consts.CONST_CONTENT_LENGTH, Consts.EMPTY_STRING_LENGTH);
            contentSha256 = Consts.EMPTY_STRING_SHA256;
        }
        headers.put(Consts.X_LOG_DATE, dateTime);
        headers.put(Consts.X_LOG_CONTENT_SHA256, contentSha256);
        Map<String, String> canonicalHeaders = new TreeMap<String, String>();
        StringBuilder signedHeaders = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            String key = header.getKey().toLowerCase();
            if (DEFAULT_SIGNED_HEADERS.contains(key)
                    || key.startsWith(Consts.CONST_X_SLS_PREFIX)
                    || key.startsWith(Consts.CONST_X_ACS_PREFIX)) {
                canonicalHeaders.put(key, header.getValue());
            }
        }
        StringBuilder headersToString = new StringBuilder();
        for (Map.Entry<String, String> header : canonicalHeaders.entrySet()) {
            if (signedHeaders.length() > 0) {
                signedHeaders.append(";");
            }
            String headerKey = header.getKey();
            signedHeaders.append(headerKey);
            headersToString.append(headerKey)
                    .append(":")
                    .append(HttpUtil.trim(header.getValue()))
                    .append(NEW_LINE);
        }
        String signedHeadersStr = signedHeaders.toString();
        String canonicalRequest = buildCanonicalRequest(httpMethod.toString(),
                resourceUri, urlParams, headersToString.toString(), signedHeadersStr, contentSha256);
        String date = dateTime.substring(0, 8);
        String scope = buildScope(date);
        String stringToSign = buildStringToSign(canonicalRequest, dateTime, scope);
        Mac mac = getMacInstance();
        HmacSHA256Hasher sha256Hasher = new HmacSHA256Hasher(mac);
        byte[] signingKey = buildSigningKey(sha256Hasher, credentials.getAccessKeySecret(), region, date);
        byte[] result = sha256Hasher.hash(signingKey, stringToSign.getBytes(CHARSET_UTF_8));
        String signature = BinaryUtil.toHex(result);
        String authorization = buildAuthorization(credentials.getAccessKeyId(), signature, scope);
//        System.out.println("canonicalRequest:\n" + canonicalRequest);
//        System.out.println("stringToSign:\n" + stringToSign);
//        System.out.println("signingKey:\n" + BinaryUtil.toHex(signingKey));
//        System.out.println("signature:\n" + signature);
//        System.out.println("authorization:\n" + authorization);
        return authorization;
    }

    private static class HmacSHA256Hasher {
        private final Mac mac;

        public HmacSHA256Hasher(Mac mac) {
            this.mac = mac;
        }

        public byte[] hash(byte[] key, byte[] data) {
            try {
                mac.init(new SecretKeySpec(key, ALGORITHM));
                return mac.doFinal(data);
            } catch (InvalidKeyException e) {
                throw new RuntimeException("Unable to calculate the signature", e);
            }
        }
    }

}
