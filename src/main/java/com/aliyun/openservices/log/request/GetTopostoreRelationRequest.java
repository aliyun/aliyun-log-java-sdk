package com.aliyun.openservices.log.request;

public class GetTopostoreRelationRequest extends TopostoreRequest{
    private String topostoreName;
    private String topostoreRelationId;

    public GetTopostoreRelationRequest(String topostoreName, String topostoreRelationId) {
        this.topostoreName = topostoreName;
        this.topostoreRelationId = topostoreRelationId;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public String getTopostoreRelationId() {
        return this.topostoreRelationId;
    }

    public void setTopostoreRelationId(String topostoreRelationId) {
        this.topostoreRelationId = topostoreRelationId;
    }

}
