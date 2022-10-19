package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Tag;

import java.io.Serializable;
import java.util.List;

public class TagResourcesRequest implements Serializable {

    private static final long serialVersionUID = 1742241059752384082L;

    private String resourceType;
    private List<String> resourceId;
    private List<Tag> tags;

    public TagResourcesRequest(String resourceType, List<String> resourceId, List<Tag> tags) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.tags = tags;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
