package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * Output logs of job execution to a logStore in
 * the same project.
 */
public class LogSetting {

    @JSONField
    private String logStore;

    public String getLogStore() {
        return logStore;
    }

    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }
}
