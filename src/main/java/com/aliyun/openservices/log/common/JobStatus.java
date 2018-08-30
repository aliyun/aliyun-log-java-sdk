package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;


public class JobStatus implements Serializable {

    @JSONField
    private long executionCount;

    @JSONField
    private long failureCount;

    @JSONField
    private Date lastExecutionTime;

    public long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }

    public long getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(long executionCount) {
        this.executionCount = executionCount;
    }

    public Date getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(Date lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobStatus jobStatus = (JobStatus) o;

        if (getExecutionCount() != jobStatus.getExecutionCount()) return false;
        if (getFailureCount() != jobStatus.getFailureCount()) return false;
        return getLastExecutionTime() != null ? getLastExecutionTime().equals(jobStatus.getLastExecutionTime()) : jobStatus.getLastExecutionTime() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getExecutionCount() ^ (getExecutionCount() >>> 32));
        result = 31 * result + (int) (getFailureCount() ^ (getFailureCount() >>> 32));
        result = 31 * result + (getLastExecutionTime() != null ? getLastExecutionTime().hashCode() : 0);
        return result;
    }
}
