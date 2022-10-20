package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.TopostoreRelation;

public class UpdateTopostoreRelationRequest extends TopostoreRequest{
    private String topostoreName;
    private TopostoreRelation topostoreRelation;

    public UpdateTopostoreRelationRequest(String topostoreName, TopostoreRelation topostoreRelation) {
        this.topostoreName = topostoreName;
        this.topostoreRelation = topostoreRelation;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public TopostoreRelation getTopostoreRelation() {
        return this.topostoreRelation;
    }

    public void setTopostoreRelation(TopostoreRelation topostoreRelation) {
        this.topostoreRelation = topostoreRelation;
    }
    
}
