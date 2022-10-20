package com.aliyun.openservices.log.response;

import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.TopostoreRelation;

public class ListTopostoreRelationResponse extends Response{

    private List<TopostoreRelation> topostoreRelations;
    private int count=0;
    private int total=0;

    public ListTopostoreRelationResponse(Map<String, String> headers,
     List<TopostoreRelation> topostoreRelations, int count, int total)  {
        super(headers);
        this.topostoreRelations = topostoreRelations;
        this.count = count;
        this.total = total;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    public List<TopostoreRelation> getTopostoreRelations() {
        return this.topostoreRelations;
    }

    public void setTopostoreRelations(List<TopostoreRelation> topostoreRelations) {
        this.topostoreRelations = topostoreRelations;
    }

}
