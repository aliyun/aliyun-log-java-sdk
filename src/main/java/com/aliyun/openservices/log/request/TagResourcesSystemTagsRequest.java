package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Tag;

import java.util.List;

public class TagResourcesSystemTagsRequest extends TagResourcesRequest {
    private String tagOwnerUid;
    private String scope;

    public TagResourcesSystemTagsRequest(String resourceType,
                                         List<String> resourceId,
                                         List<Tag> tags,
                                         String tagOwnerUid) {
        super(resourceType, resourceId, tags);
        this.tagOwnerUid = tagOwnerUid;
    }

    public TagResourcesSystemTagsRequest(String resourceType,
                                         List<String> resourceId,
                                         List<Tag> tags,
                                         String tagOwnerUid,
                                         String scope) {
        super(resourceType, resourceId, tags);
        this.tagOwnerUid = tagOwnerUid;
        this.scope = scope;
    }

    public String getTagOwnerUid() {
        return tagOwnerUid;
    }

    public void setTagOwnerUid(String tagOwnerUid) {
        this.tagOwnerUid = tagOwnerUid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
