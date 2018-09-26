package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;


public class JobHistory implements Serializable {

    @JSONField
    private String id;

    @JSONField
    private String jobName;

    @JSONField
    private JobExecutionStatus status;

    @JSONField
    private Date startedAt;

    @JSONField
    private Date stoppedAt;

    @JSONField
    private String message;

    @JSONField
    private Integer retryCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(Date stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public JobExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(JobExecutionStatus status) {
        this.status = status;
    }

    public enum JobExecutionStatus {
        Failed,
        Successed
    }
}
