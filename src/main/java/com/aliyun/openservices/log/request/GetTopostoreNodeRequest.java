package com.aliyun.openservices.log.request;

public class GetTopostoreNodeRequest extends TopostoreRequest{
    private String topostoreName;
    private String topostoreNodeId;

    public GetTopostoreNodeRequest(String topostoreName, String topostoreNodeId) {
        this.topostoreName = topostoreName;
        this.topostoreNodeId = topostoreNodeId;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public String getTopostoreNodeId() {
        return this.topostoreNodeId;
    }

    public void setTopostoreNodeId(String topostoreNodeId) {
        this.topostoreNodeId = topostoreNodeId;
    }

}
