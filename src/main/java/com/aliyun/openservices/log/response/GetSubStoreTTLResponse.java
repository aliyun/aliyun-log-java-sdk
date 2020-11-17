package com.aliyun.openservices.log.response;

import java.util.Map;

public class GetSubStoreTTLResponse extends Response {

    private int ttl;
    /**
     * Construct the base response body with http headers
     *
     * @param headers http headers
     */
    public GetSubStoreTTLResponse(Map<String, String> headers) {
        super(headers);
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
