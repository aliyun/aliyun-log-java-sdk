package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListMaterializedViewsResponse extends Response {

    private List<String> materializedViews = Collections.emptyList();
    private int total;
    private int count;

    public ListMaterializedViewsResponse(Map<String, String> headers) {
        super(headers);
    }

    public void setMaterializedViews(List<String> externalStores) {
        this.materializedViews = new ArrayList<String>(externalStores);
    }

    public List<String> getMaterializedViews() {
        return materializedViews;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
