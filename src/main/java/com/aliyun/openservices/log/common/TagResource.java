package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class TagResource implements Serializable {
    private static final long serialVersionUID = 1871783791415724270L;

    private String resourceId;
    private String resourceType;
    private String tagKey;
    private String tagValue;

    public TagResource(String resourceId, String resourceType, String tagKey, String tagValue) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.tagKey = tagKey;
        this.tagValue = tagValue;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getTagKey() {
        return tagKey;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    @InternalApi
    public static TagResource fromJsonObject(JSONObject object) {
        return new TagResource(
                object.getString("resourceId"),
                object.getString("resourceType"),
                object.getString("tagKey"),
                object.getString("tagValue"));
    }

}
