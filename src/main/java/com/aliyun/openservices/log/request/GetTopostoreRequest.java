package com.aliyun.openservices.log.request;

public class GetTopostoreRequest extends TopostoreRequest {
    private String topostoreName;

    public GetTopostoreRequest(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }
    
}
