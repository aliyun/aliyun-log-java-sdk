package com.aliyun.openservices.log.request;

public class GetSubStoreTTLResquest extends Request{

    private String logstoreName;
    /**
     * Construct the base request
     *
     * @param project project name
     */
    public GetSubStoreTTLResquest(String project,String logstoreName) {
        super(project);
        this.logstoreName = logstoreName;
    }

    public String getLogstoreName() {
        return logstoreName;
    }

    public void setLogstoreName(String logstoreName) {
        this.logstoreName = logstoreName;
    }
}
