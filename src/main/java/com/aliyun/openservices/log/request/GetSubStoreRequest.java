package com.aliyun.openservices.log.request;

public class GetSubStoreRequest extends Request {
    private String logstoreName;
    private String subStoreName;

    public GetSubStoreRequest(String project, String logstoreName, String subStoreName) {
        super(project);
        this.logstoreName = logstoreName;
        this.subStoreName = subStoreName;
    }

    public String getLogstoreName() {
        return logstoreName;
    }

    public void setLogstoreName(String logstoreName) {
        this.logstoreName = logstoreName;
    }

    public String getSubStoreName() {
        return subStoreName;
    }

    public void setSubStoreName(String subStoreName) {
        this.subStoreName = subStoreName;
    }
}
