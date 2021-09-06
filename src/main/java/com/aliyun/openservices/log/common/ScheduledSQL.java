package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import java.io.Serializable;
public class ScheduledSQL extends ScheduledJob implements Serializable {
    private static final long serialVersionUID = 9045820359511405750L;
    @JSONField
    private String scheduleId;
    public ScheduledSQL() {
        setType(JobType.SCHEDULED_SQL);
    }
    @JSONField
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
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        scheduleId = JsonUtils.readOptionalString(jsonObject,"scheduleId","");
        configuration = new ScheduledSQLConfiguration();
        configuration.deserialize(jsonObject.getJSONObject("configuration"));
    }
}
