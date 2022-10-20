package com.aliyun.openservices.log.request;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.TopostoreNode;

public class UpsertTopostoreNodeRequest extends TopostoreRequest{
    private String topostoreName;
    private List<TopostoreNode> topostoreNodes;


    public UpsertTopostoreNodeRequest(String topostoreName, List<TopostoreNode> topostoreNodes) {
        this.topostoreName = topostoreName;
        this.topostoreNodes = topostoreNodes;
    }

    public String getPostBody() {
        JSONObject result = new JSONObject();
        JSONArray encodedNodes = new JSONArray();
        encodedNodes.addAll(topostoreNodes);
        result.put("nodes", encodedNodes);
        return result.toString();
    }
    
    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public List<TopostoreNode> getTopostoreNodes() {
        return this.topostoreNodes;
    }

    public void setTopostoreNodes(List<TopostoreNode> topostoreNodes) {
        this.topostoreNodes = topostoreNodes;
    }

}
