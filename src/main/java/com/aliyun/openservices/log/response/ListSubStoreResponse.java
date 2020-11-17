package com.aliyun.openservices.log.response;

import java.util.List;
import java.util.Map;

public class ListSubStoreResponse extends Response {
    private List<String> subStoreNames;

    public ListSubStoreResponse(Map<String, String> headers) {
        super(headers);
    }

    public List<String> getSubStoreNames() {
        return subStoreNames;
    }

    public void setSubStoreNames(List<String> subStoreNames) {
        this.subStoreNames = subStoreNames;
    }
}
