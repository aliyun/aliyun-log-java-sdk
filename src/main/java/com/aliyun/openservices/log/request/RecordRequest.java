package com.aliyun.openservices.log.request;

public class RecordRequest extends ResourceRequest {

    private String resourceName;

    public RecordRequest(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
