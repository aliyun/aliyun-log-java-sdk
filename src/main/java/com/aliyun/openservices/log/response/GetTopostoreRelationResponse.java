package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.TopostoreRelation;

public class GetTopostoreRelationResponse extends Response {
    private TopostoreRelation topostoreRelation;

    public GetTopostoreRelationResponse(Map<String, String> headers, TopostoreRelation topostoreRelation) {
        super(headers);
        this.topostoreRelation = topostoreRelation;
    }

    public TopostoreRelation getTopostoreRelation() {
        return this.topostoreRelation;
    }

    public void setTopostoreRelation(TopostoreRelation topostoreRelation) {
        this.topostoreRelation = topostoreRelation;
    }

}
