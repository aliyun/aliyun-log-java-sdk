package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class AuditJob extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = 5950790729563144144L;

    private AuditJobConfiguration configuration;

    public AuditJob() {
        setType(JobType.AUDIT_JOB);
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.RESIDENT);
        setSchedule(schedule);
    }

    public void setConfiguration(AuditJobConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public JobConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        configuration = new AuditJobConfiguration();
        configuration.fromJsonObject(value.getJSONObject("configuration"));
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = new JSONObject();
        value.put("name", getName());
        value.put("type", getType().toString());
        value.put("displayName", getDisplayName());
        value.put("description", getDescription());
        JSONObject scheduleJson = new JSONObject();
        scheduleJson.put("type", getSchedule().getType().toString());
        value.put("schedule", scheduleJson);
        value.put("configuration", this.configuration.toJsonObject());
        return value;
    }
}
