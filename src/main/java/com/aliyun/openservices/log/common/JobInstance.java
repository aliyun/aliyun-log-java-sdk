package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import java.io.Serializable;
public class JobInstance implements Serializable {
    private static final long serialVersionUID = 949227748635414993L;
    @JSONField
    private String instanceId;
    @JSONField
    private String jobName;
    @JSONField
    private String jobScheduleId;
    @JSONField
    private String displayName;
    @JSONField
    private String description;
    @JSONField
    private long createTimeInMillis;
    @JSONField
    private long beginTimeInMillis;
    @JSONField
    private long updateTimeInMillis;
    @JSONField
    private long scheduleTimeInMillis;
    @JSONField
    private String state;
    @JSONField
    private String errorCode;
    @JSONField
    private String errorMessage;
    @JSONField
    private String summary;
    @JSONField
    private String result;
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobScheduleId() {
        return jobScheduleId;
    }
    public void setJobScheduleId(String jobScheduleId) {
        this.jobScheduleId = jobScheduleId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public long getCreateTimeInMillis() {
        return createTimeInMillis;
    }
    public void setCreateTimeInMillis(long createTimeInMillis) {
        this.createTimeInMillis = createTimeInMillis;
    }
    public long getScheduleTimeInMillis() {
        return scheduleTimeInMillis;
    }
    public void setScheduleTimeInMillis(int scheduleTimeInMillis) {
        this.scheduleTimeInMillis = scheduleTimeInMillis;
    }
    public long getUpdateTimeInMillis() {
        return updateTimeInMillis;
    }
    public void setUpdateTimeInMillis(int updateTimeInMillis) {
        this.updateTimeInMillis = updateTimeInMillis;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }

    public long getBeginTimeInMillis() {
        return beginTimeInMillis;
    }

    public void setBeginTimeInMillis(long beginTimeInMillis) {
        this.beginTimeInMillis = beginTimeInMillis;
    }

    public void deserialize(JSONObject value) {
        instanceId = value.getString("instanceId");
        jobScheduleId = JsonUtils.readOptionalString(value, "jobScheduleId", "");
        jobName = JsonUtils.readOptionalString(value, "jobName", "");
        displayName = JsonUtils.readOptionalString(value, "displayName", "");
        description = JsonUtils.readOptionalString(value, "description", "");
        createTimeInMillis = JsonUtils.readOptionalInt(value,"createTimeInMillis")!=null ? value.getLong("createTimeInMillis"):0;
        beginTimeInMillis = JsonUtils.readOptionalInt(value,"beginTimeInMillis")!=null ? value.getLong("beginTimeInMillis"):0;
        updateTimeInMillis = JsonUtils.readOptionalInt(value,"updateTimeInMillis")!=null ? value.getLong("updateTimeInMillis"):0;
        scheduleTimeInMillis = JsonUtils.readOptionalInt(value,"scheduleTimeInMillis")!=null ? value.getLong("scheduleTimeInMillis"):0;
        state = value.getString("state");
        errorCode = value.getString("errorCode");
        errorMessage = value.getString("errorMessage");
        summary = JsonUtils.readOptionalString(value, "summary", "");
        result = value.getString("result");
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobInstance that = (JobInstance) o;
        if (getInstanceId() != null ? !getInstanceId().equals(that.getInstanceId()) : that.getInstanceId() != null) {
            return false;
        }
        if (getJobName() != null ? !getJobName().equals(that.getJobName()) : that.getJobName() != null) {
            return false;
        }
        if (getDisplayName() != null ? !getDisplayName().equals(that.getDisplayName()) : that.getDisplayName() != null) {
            return false;
        }
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null) {
            return false;
        }
        if (getJobScheduleId() != null ? !getJobScheduleId().equals(that.getJobScheduleId()) : that.getJobScheduleId() != null) {
            return false;
        }
        if (getCreateTimeInMillis() != that.getCreateTimeInMillis()) {
            return false;
        }
        if (getUpdateTimeInMillis() != that.getUpdateTimeInMillis()) {
            return false;
        }
        if (getScheduleTimeInMillis() != that.getScheduleTimeInMillis()) {
            return false;
        }
        if (getState() != null ? !getState().equals(that.getState()) : that.getState() != null) {
            return false;
        }
        if (getErrorCode() != null ? !getErrorCode().equals(that.getErrorCode()) : that.getErrorCode() != null) {
            return false;
        }
        if (getErrorMessage() != null ? !getErrorMessage().equals(that.getErrorMessage()) : that.getErrorMessage() != null) {
            return false;
        }
        if (getSummary() != null ? !getSummary().equals(that.getSummary()) : that.getSummary() != null) {
            return false;
        }
        return getResult() != null ? !getResult().equals(that.getResult()) : that.getResult() != null;
    }
    @Override
    public int hashCode() {
        int result = getInstanceId() != null ? getInstanceId().hashCode() : 0;
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getJobScheduleId() != null ? getJobScheduleId().hashCode() : 0);
        result = (int) (31 * result + getCreateTimeInMillis());
        result = (int) (31 * result + getUpdateTimeInMillis());
        result = (int) (31 * result + getScheduleTimeInMillis());
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getErrorCode() != null ? getErrorCode().hashCode() : 0);
        result = 31 * result + (getErrorMessage() != null ? getErrorMessage().hashCode() : 0);
        result = 31 * result + (getSummary() != null ? getSummary().hashCode() : 0);
        result = 31 * result + (getResult() != null ? getResult().hashCode() : 0);
        return result;
    }
}