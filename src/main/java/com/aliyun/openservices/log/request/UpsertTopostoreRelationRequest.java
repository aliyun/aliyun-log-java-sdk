package com.aliyun.openservices.log.request;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.TopostoreRelation;

public class UpsertTopostoreRelationRequest extends TopostoreRequest{
    private String topostoreName;
    private List<TopostoreRelation> topostoreRelations;


    public UpsertTopostoreRelationRequest(String topostoreName, List<TopostoreRelation> topostoreRelations) {
        this.topostoreName = topostoreName;
        this.topostoreRelations = topostoreRelations;
    }

    public String getPostBody() {
        JSONObject result = new JSONObject();
        JSONArray encodedRelations = new JSONArray();
        encodedRelations.addAll(topostoreRelations);
        result.put("relations", encodedRelations);
        return result.toString();
    }
    
    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public List<TopostoreRelation> getTopostoreRelations() {
        return this.topostoreRelations;
    }

    public void setTopostoreRelations(List<TopostoreRelation> topostoreRelations) {
        this.topostoreRelations = topostoreRelations;
    }

}
