package com.aliyun.openservices.log.request;

public class DeleteTopostoreRequest extends TopostoreRequest{
    private String topostoreName;

    public DeleteTopostoreRequest(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

}
