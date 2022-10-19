package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class UntagResourcesRequest implements Serializable {

    private static final long serialVersionUID = 7380775619459713801L;

    private String resourceType;
    private List<String> resourceId;
    private List<String> tags;
    @JSONField(name = "all")
    private boolean all;

    public UntagResourcesRequest(String resourceType, List<String> resourceId, List<String> tags) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.tags = tags;
        this.all = false;
    }

    public UntagResourcesRequest(String resourceType, List<String> resourceId) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.all = true;
    }

    public List<String> getResourceId() {
        return resourceId;
    }

    public void setResourceId(List<String> resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }
}
