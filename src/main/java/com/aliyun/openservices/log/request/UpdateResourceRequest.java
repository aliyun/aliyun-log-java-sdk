package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Resource;

public class UpdateResourceRequest extends ResourceRequest {
    private static final long serialVersionUID = 508606469423400062L;

    private Resource resource = null;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public UpdateResourceRequest(Resource resource) {
        this.resource = resource;
    }
}
