package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.List;


public class Alert extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = 9211926785430833230L;

    @JSONField
    private AlertConfiguration configuration;

    public Alert() {
        setType(JobType.ALERT);
    }

    @Override
    public AlertConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AlertConfiguration configuration) {
        this.configuration = configuration;
    }

    public void deserialize(JSONObject value) {
        super.deserialize(value);
        configuration = new AlertConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
    }

    public void validate() {
        Args.notNullOrEmpty(getName(), "name");
        Args.notNullOrEmpty(getDisplayName(), "displayName");
        Args.notNull(configuration, "configuration");
        List<Query> queries = configuration.getQueryList();
        Args.notNullOrEmpty(queries, "Query list");
        for (Query query : queries) {
            Args.notNull(query, "query");
        }
        Args.notNull(getSchedule(), "schedule");
    }
}
