package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;


public abstract class ScheduledJob extends AbstractJob {

    /**
     * @deprecated use {@code status} instead.
     * Use status instead.
     */
    @Deprecated
    private JobState state;

    private String status;

    private JobSchedule schedule;

    @Deprecated
    public JobState getState() {
        return state;
    }

    @Deprecated
    public void setState(JobState state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JobSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(JobSchedule schedule) {
        this.schedule = schedule;
    }

    public void fromJsonString(String jobString) throws LogException {
        try {
            fromJsonObject(JSONObject.parseObject(jobString));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateJob", e.getMessage(), e, "");
        }
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (state != null) {
            value.put("state", state.toString());
        }
        put(value, "status", status);
        if (schedule != null) {
            value.put("schedule", schedule.toJsonObject());
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        state = JobState.fromString(value.getString("state"));
        status = JsonUtils.readOptionalString(value, "status");
        schedule = new JobSchedule();
        schedule.fromJsonObject(value.getJSONObject("schedule"));
    }
}
