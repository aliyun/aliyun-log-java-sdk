package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Resource;

import java.util.Map;

public class GetResourceResponse extends Response {

    private Resource resource;
    public GetResourceResponse(Map<String, String> headers, Resource resource) {
        super(headers);
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
