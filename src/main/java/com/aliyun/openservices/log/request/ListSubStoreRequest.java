package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListSubStoreRequest extends Request {

    private String logstoreName;

    public ListSubStoreRequest(String project, String logstoreName) {
        super(project);
        this.logstoreName = logstoreName;
    }

    public void setLogstoreName(String logstoreName) {
        this.logstoreName = logstoreName;
    }

    public String getLogstoreName() {
        return logstoreName;
    }
}
