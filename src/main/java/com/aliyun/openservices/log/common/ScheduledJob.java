package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;


public abstract class ScheduledJob extends AbstractJob {

    /**
     * Job state.
     */
    private JobState state;

    /**
     * How to trigger job.
     */
    private JobSchedule schedule;

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
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
        schedule = new JobSchedule();
        schedule.deserialize(value.getJSONObject("schedule"));
    }
}
