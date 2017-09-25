package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class EtlTriggerConfig implements Serializable {

    private static final long serialVersionUID = -3419620921893500865L;
    private String roleArn;
    private int triggerInterval;
    private int maxRetryTime;

    public EtlTriggerConfig(String roleArn, int triggerInterval, int maxRetryTime) {
        this.roleArn = roleArn;
        this.triggerInterval = triggerInterval;
        this.maxRetryTime = maxRetryTime;
    }

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public int getTriggerInterval() {
        return triggerInterval;
    }

    public void setTriggerInterval(int triggerInterval) {
        this.triggerInterval = triggerInterval;
    }

    public int getMaxRetryTime() {
        return maxRetryTime;
    }

    public void setMaxRetryTime(int maxRetryTime) {
        this.maxRetryTime = maxRetryTime;
    }
}
