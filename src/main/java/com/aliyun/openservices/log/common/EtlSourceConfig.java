package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class EtlSourceConfig implements Serializable {

    private static final long serialVersionUID = 6803258295904365544L;
    private String logstoreName;

    public String getLogstoreName() {
        return logstoreName;
    }

    public void setLogstoreName(String logstoreName) {
        this.logstoreName = logstoreName;
    }

    public EtlSourceConfig(String logstoreName) {

        this.logstoreName = logstoreName;
    }
}
