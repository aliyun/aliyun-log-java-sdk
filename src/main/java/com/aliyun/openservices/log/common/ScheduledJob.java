package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;


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

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        state = JobState.fromString(value.getString("state"));
        status = JsonUtils.readOptionalString(value, "status");
        schedule = new JobSchedule();
        schedule.deserialize(value.getJSONObject("schedule"));
    }
}
