package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;
import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;
public class ScheduledSQL extends ScheduledJob implements Serializable {
    private static final long serialVersionUID = 9045820359511405750L;
    private String scheduleId;
    public ScheduledSQL() {
        setType(JobType.SCHEDULED_SQL);
    }
    private ScheduledSQLConfiguration configuration;
    public String getScheduleId() {
        return scheduleId;
    }
    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }
    @Override
    public ScheduledSQLConfiguration getConfiguration() {
        return configuration;
    }
    public void setConfiguration(ScheduledSQLConfiguration configuration) {
        this.configuration = configuration;
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
        configuration = new ScheduledSQLConfiguration();
        configuration.fromJsonObject(jsonObject.getJSONObject("configuration"));
    }
}
