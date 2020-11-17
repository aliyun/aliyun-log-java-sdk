package com.aliyun.openservices.log.request;

public class DeleteSubStoreRequest extends Request {
    private String logStoreName;
    private String subStoreName;

    public DeleteSubStoreRequest(String project) {
        super(project);
    }

    public DeleteSubStoreRequest(String project, String logStoreName, String subStoreName) {
        super(project);
        this.logStoreName = logStoreName;
        this.subStoreName = subStoreName;
    }

    public String getLogStoreName() {
        return logStoreName;
    }

    public void setLogStoreName(String logStoreName) {
        this.logStoreName = logStoreName;
    }

    public String getSubStoreName() {
        return subStoreName;
    }

    public void setSubStoreName(String subStoreName) {
        this.subStoreName = subStoreName;
    }
}
