package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.SubStore;

import java.util.Map;

public class GetSubStoreResponse extends Response {

    private SubStore subStore;

    public GetSubStoreResponse(Map<String, String> headers, SubStore subStore) {
        super(headers);
        setSubStore(subStore);
    }

    public SubStore getSubStore() {
        return subStore;
    }

    public void setSubStore(SubStore subStore) {
        this.subStore = subStore;
    }
}
