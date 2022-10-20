package com.aliyun.openservices.log.request;

import java.util.List;

public class UntagResourcesSystemTagsRequest extends UntagResourcesRequest {
    private String tagOwnerUid;

    public UntagResourcesSystemTagsRequest(String resourceType,
                                           List<String> resourceId,
                                           List<String> tags,
                                           String tagOwnerUid) {
        super(resourceType, resourceId, tags);
        this.tagOwnerUid = tagOwnerUid;
    }

    public UntagResourcesSystemTagsRequest(String resourceType,
                                           List<String> resourceId,
                                           String tagOwnerUid) {
        super(resourceType, resourceId);
        this.tagOwnerUid = tagOwnerUid;
    }

    public String getTagOwnerUid() {
        return tagOwnerUid;
    }

    public void setTagOwnerUid(String tagOwnerUid) {
        this.tagOwnerUid = tagOwnerUid;
    }
}
