package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class EtlLogConfig implements Serializable {

    private static final long serialVersionUID = 4701648321761962334L;
    private String endpoint;
    private String projectName;
    private String logstoreName;

    public EtlLogConfig(String endpoint, String projectName, String logstoreName) {
        this.endpoint = endpoint;
        this.projectName = projectName;
        this.logstoreName = logstoreName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setLogstoreName(String logstoreName) {
        this.logstoreName = logstoreName;
    }

    public String getLogstoreName() {
        return logstoreName;
    }
}





