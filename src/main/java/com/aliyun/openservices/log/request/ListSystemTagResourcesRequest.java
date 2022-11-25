package com.aliyun.openservices.log.request;

import java.util.List;
import java.util.Map;

public class ListSystemTagResourcesRequest extends ListTagResourcesRequest {



    public ListSystemTagResourcesRequest(String resourceType,
                                         List<String> resourceIdList,
                                         Map<String, String> tagList,
                                         String tagOwnerUid,
                                         String category,
                                         String scope) {
        super(resourceType, resourceIdList, tagList);
        setTagOwnerUid(tagOwnerUid);
        setCategory(category);
        setScope(scope);
    }

    public void setTagOwnerUid(String tagOwnerUid) {
        SetParam("tagOwnerUid", tagOwnerUid);
    }

    public String getTagOwnerUid() {
        return GetParam("tagOwnerUid");
    }

    public void setCategory(String category) {
        SetParam("category", category);
    }

    public String getCategory() {
        return GetParam("category");
    }

    public void setScope(String scope) {
        SetParam("scope", scope);
    }

    public String getScope() {
        return GetParam("scope");
    }
}
