package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.TopostoreNode;

public class GetTopostoreNodeResponse extends Response {
    private TopostoreNode topostoreNode;

    public GetTopostoreNodeResponse(Map<String, String> headers, TopostoreNode topostoreNode) {
        super(headers);
        this.topostoreNode = topostoreNode;
    }

    public TopostoreNode getTopostoreNode() {
        return this.topostoreNode;
    }

    public void setTopostoreNode(TopostoreNode topostoreNode) {
        this.topostoreNode = topostoreNode;
    }

}
