package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Topostore;

public class ListTopostoreResponse extends Response{
    private int count = 0;
    private int total = 0;
    private List<Topostore> topostores = new ArrayList<Topostore>();

    public ListTopostoreResponse(Map<String, String> headers,  List<Topostore> topostores, int count, int total) {
        super(headers);
        setCount(count);
        setTotal(total);
        setTopostores(topostores);
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

    public List<Topostore> getTopostores() {
        return this.topostores;
    }

    public void setTopostores(List<Topostore> topostores) {
        this.topostores = topostores;
    }    
}
