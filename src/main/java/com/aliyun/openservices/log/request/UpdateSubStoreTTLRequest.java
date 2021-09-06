package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class UpdateSubStoreTTLRequest extends Request{
    private String logstoreName;
    private int ttl;
    /**
     * Construct the base request
     *
     * @param project project name
     */
    public UpdateSubStoreTTLRequest(String project,String logstoreName,int ttl) {
        super(project);
        this.logstoreName = logstoreName;
        setTtl(ttl);
    }

    public String getLogstoreName() {
        return logstoreName;
    }

    public void setLogstoreName(String logstoreName) {
        this.logstoreName = logstoreName;
    }

    public void setTtl(int ttl) {
        SetParam(Consts.CONST_TTL, String.valueOf(ttl));
    }
}
