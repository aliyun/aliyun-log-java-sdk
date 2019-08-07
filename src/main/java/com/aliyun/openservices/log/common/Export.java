package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

import java.io.Serializable;

public class Export extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = 9045820359511405750L;

    public Export() {
        setType(JobType.EXPORT);
        JobSchedule schedule = new JobSchedule();
        schedule.setType(JobScheduleType.RESIDENT);
        setSchedule(schedule);
    }

    private ExportConfiguration configuration;

    @Override
    public ExportConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ExportConfiguration configuration) {
        this.configuration = configuration;
    }

    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        configuration = new ExportConfiguration();
        configuration.deserialize(jsonObject.getJSONObject("configuration"));
    }
}
