package com.aliyun.openservices.log.request;

import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

public class DeleteTopostoreRelationRequest extends TopostoreRequest{
    private String topostoreName;
    private List<String> topostoreRelationIds;

    public DeleteTopostoreRelationRequest(String topostoreName, List<String> relationIds) {
        this.topostoreName = topostoreName;
        this.topostoreRelationIds = relationIds;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public List<String> getTopostoreRelationIds() {
        return this.topostoreRelationIds;
    }

    public void setTopostoreRelationIds(List<String> topostoreRelationIds) {
        this.topostoreRelationIds = topostoreRelationIds;
    }

    @Override
    public Map<String, String> GetAllParams() {
        SetParam(Consts.TOPOSTORE_RELATION_ID_LIST, Utils.join(",", topostoreRelationIds));
        return super.GetAllParams();
    }

}
