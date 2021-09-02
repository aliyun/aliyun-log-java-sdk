package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListResourceResponse extends Response {

    protected int count = 0;
    protected int total = 0;
    protected List<Resource> resources = new ArrayList<Resource>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public ListResourceResponse(Map<String, String> headers, int count, int total, List<Resource> resources) {
        super(headers);
        setCount(count);
        setTotal(total);
        this.resources = resources;
    }
}
