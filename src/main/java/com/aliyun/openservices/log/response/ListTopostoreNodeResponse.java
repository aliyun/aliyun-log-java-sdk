package com.aliyun.openservices.log.response;

import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.TopostoreNode;

public class ListTopostoreNodeResponse extends Response{

    private List<TopostoreNode> topostoreNodes;
    private int count = 0;
    private int total = 0;


    public ListTopostoreNodeResponse(Map<String, String> headers, 
        List<TopostoreNode> topostoreNodes,int count, int total)  {
        super(headers);
        this.topostoreNodes = topostoreNodes;
        this.count = count;
        this.total = total;
    }

    public List<TopostoreNode> getTopostoreNodes() {
        return this.topostoreNodes;
    }

    public void setTopostoreNodes(List<TopostoreNode> topostoreNodes) {
        this.topostoreNodes = topostoreNodes;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
