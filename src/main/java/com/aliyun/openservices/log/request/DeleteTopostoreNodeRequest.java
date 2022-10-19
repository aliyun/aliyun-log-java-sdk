package com.aliyun.openservices.log.request;

import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

public class DeleteTopostoreNodeRequest extends TopostoreRequest{
    private String topostoreName;
    private List<String> topostoreNodeIds;

    public DeleteTopostoreNodeRequest(String topostoreName, List<String> nodeIds) {
        this.topostoreName = topostoreName;
        this.topostoreNodeIds = nodeIds;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public List<String> getTopostoreNodeIds() {
        return this.topostoreNodeIds;
    }

    public void setTopostoreNodeIds(List<String> topostoreNodeIds) {
        this.topostoreNodeIds = topostoreNodeIds;
    }

    @Override
    public Map<String, String> GetAllParams() {
        SetParam(Consts.TOPOSTORE_NODE_ID_LIST, Utils.join(",", topostoreNodeIds));
        return super.GetAllParams();
    }

}
