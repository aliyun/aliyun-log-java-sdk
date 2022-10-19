package com.aliyun.openservices.log.request;

import java.util.List;

import com.aliyun.openservices.log.common.TopostoreNode;
import com.aliyun.openservices.log.common.TopostoreRelation;

public class ListTopostoreNodeRelationResponse {
    private List<TopostoreNode> nodes;
    private List<TopostoreRelation> relations;


    public ListTopostoreNodeRelationResponse() {
    }

    public ListTopostoreNodeRelationResponse(List<TopostoreNode> nodes, List<TopostoreRelation> relations) {
        this.nodes = nodes;
        this.relations = relations;
    }

    public List<TopostoreNode> getNodes() {
        return this.nodes;
    }

    public void setNodes(List<TopostoreNode> nodes) {
        this.nodes = nodes;
    }

    public List<TopostoreRelation> getRelations() {
        return this.relations;
    }

    public void setRelations(List<TopostoreRelation> relations) {
        this.relations = relations;
    }


}
