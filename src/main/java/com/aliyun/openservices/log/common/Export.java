package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class Export extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = 9045820359511405750L;

    public Export() {
        setType(JobType.EXPORT);
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.RESIDENT);
        setSchedule(schedule);
    }

    private ExportConfiguration configuration;

    private String scheduleId;

    @Override
    public ExportConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ExportConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        put(value, "scheduleId", scheduleId);
        return value;
    }

    @InternalApi
    public void fromJsonObject(JSONObject jsonObject) {
        super.fromJsonObject(jsonObject);
        scheduleId = JsonUtils.readOptionalString(jsonObject,"scheduleId","");
        configuration = new ExportConfiguration();
        configuration.fromJsonObject(jsonObject.getJSONObject("configuration"));
    }
}
