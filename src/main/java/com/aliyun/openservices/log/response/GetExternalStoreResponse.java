package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ExternalStore;

import java.util.Map;

public class GetExternalStoreResponse extends Response {

    private static final long serialVersionUID = -1995520101064642113L;

    private ExternalStore externalStore = new ExternalStore();

    public GetExternalStoreResponse(Map<String, String> headers, ExternalStore externalStore) {
        super(headers);
        setExternalStore(externalStore);
    }

    public ExternalStore getExternalStore() {
        return externalStore;
    }

    public void setExternalStore(ExternalStore externalStore) {
        this.externalStore = externalStore;
    }
}
