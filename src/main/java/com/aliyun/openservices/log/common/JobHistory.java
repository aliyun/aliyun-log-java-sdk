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

    private ExecutionDetails executionDetails;

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

    public ExecutionDetails getExecutionDetails() {
        return executionDetails;
    }

    public void setExecutionDetails(ExecutionDetails executionDetails) {
        this.executionDetails = executionDetails;
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
            throw new IllegalArgumentException("Unknown execution status: " + value);
        }
    }

    public void deserialize(final JSONObject value) {
        id = value.getString("id");
        jobName = value.getString("jobName");
        jobType = JobType.fromString(value.getString("jobType"));
        startedAt = new Date(value.getLong("startedAt"));
        completedAt = new Date(value.getLong("completedAt"));
        status = ExecutionStatus.fromString(value.getString("status"));
        if (value.containsKey("errorMessage")) {
            errorMessage = value.getString("errorMessage");
        }
        if (value.containsKey("executionDetails")) {
            switch (jobType) {
                case Alert:
                    executionDetails = new AlertDetails();
                    executionDetails.deserialize(value.getJSONObject("executionDetails"));
                    break;
                default:
                    throw new IllegalArgumentException("Unimplemented job type: " + jobType);
            }
        }
    }
}
