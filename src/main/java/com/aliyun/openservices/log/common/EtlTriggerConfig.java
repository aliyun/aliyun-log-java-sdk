package com.aliyun.openservices.log.common;

import java.io.Serializable;

public class EtlTriggerConfig implements Serializable {

    private static final long serialVersionUID = -3419620921893500865L;
    private String roleArn;
    private int triggerInterval;
    private int maxRetryTime;
    private String startingPosition = null;
    private long startingUnixtime = -1;

    public EtlTriggerConfig(String roleArn, int triggerInterval, int maxRetryTime) {
        this.roleArn = roleArn;
        this.triggerInterval = triggerInterval;
        this.maxRetryTime = maxRetryTime;
    }

    public EtlTriggerConfig(String roleArn, int triggerInterval, int maxRetryTime, String startingPosition, long startingUnixtime) {
        this.roleArn = roleArn;
        this.triggerInterval = triggerInterval;
        this.maxRetryTime = maxRetryTime;
        this.startingPosition = startingPosition;
        this.startingUnixtime = startingUnixtime;
    }

    public void setStartFromLastest() {
        this.startingPosition = Consts.ETL_JOB_TRIGGER_STARTING_POSITION_LATEST;
        this.startingUnixtime = -1;
    }

    public void setStartFromUnixtime(long startingUnixtime) {
        this.startingPosition = Consts.ETL_JOB_TRIGGER_STARTING_POSITION_AT_UNIXTIME;
        this.startingUnixtime = startingUnixtime;
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

    public String getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(String startingPosition) {
        this.startingPosition = startingPosition;
    }

    public long getStartingUnixtime() {
        return startingUnixtime;
    }

    public void setStartingUnixtime(long startingUnixtime) {
        this.startingUnixtime = startingUnixtime;
    }
}
