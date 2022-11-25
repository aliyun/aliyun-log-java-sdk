package com.aliyun.openservices.log.http.signer;

import com.aliyun.openservices.log.http.client.HttpMethod;

import java.util.Map;

public interface SlsSigner {

    void sign(HttpMethod httpMethod,
              Map<String, String> headers,
              String resourceUri,
              Map<String, String> urlParams,
              byte[] body);
}
