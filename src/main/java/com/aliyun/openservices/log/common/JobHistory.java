package com.aliyun.openservices.log.common;


import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;


public class JobHistory implements Serializable {

    private String id;

    private String jobName;

    private JobType jobType;

    private ExecutionStatus status;

    private Date startedAt;

    private Date completedAt;

    private String errorMessage;

    private ExecutionResult result;

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

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public ExecutionResult getResult() {
        return result;
    }

    public void setResult(ExecutionResult result) {
        this.result = result;
    }

    public enum ExecutionStatus {
        FAILED("Failed"),
        SUCCEED("Succeed");

        private final String value;

        ExecutionStatus(String value) {
            this.value = value;
        }

        public static ExecutionStatus fromString(final String value) {
            for (ExecutionStatus status : ExecutionStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }

    private static Date parseDate(String date) {
        return new Date(Long.parseLong(date));
    }

    public void deserialize(final JSONObject value) {
        id = value.getString("id");
        jobName = value.getString("jobName");
        jobType = JobType.fromString(value.getString("jobType"));
        completedAt = parseDate(value.getString("completedAt"));
        startedAt = parseDate(value.getString("startedAt"));
        status = ExecutionStatus.fromString(value.getString("status"));
        if (value.containsKey("errorMessage")) {
            errorMessage = value.getString("errorMessage");
        }
        switch (jobType) {
            case Alert:
                result = new AlertResult();
                result.deserialize(value.getJSONObject("result"));
                break;
            default:
                break;
        }
    }
}
