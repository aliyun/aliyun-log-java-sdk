package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.TopostoreNode;

public class CreateTopostoreNodeRequest extends TopostoreRequest {
    private String topostoreName;
    private TopostoreNode topostoreNode;

    public CreateTopostoreNodeRequest(String topostoreName, TopostoreNode topostoreNode) {
        this.topostoreName = topostoreName;
        this.topostoreNode = topostoreNode;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public TopostoreNode getTopostoreNode() {
        return this.topostoreNode;
    }

    public void setTopostoreNode(TopostoreNode topostoreNode) {
        this.topostoreNode = topostoreNode;
    }
    
}
