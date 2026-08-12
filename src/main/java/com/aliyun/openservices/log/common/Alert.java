package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import com.aliyun.openservices.log.annotation.InternalApi;


public class Alert extends ScheduledJob implements Serializable {

    private static final long serialVersionUID = 9211926785430833230L;

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

    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        configuration = new AlertConfiguration();
        configuration.fromJsonObject(value.getJSONObject("configuration"));
    }

    public void validate() {
        Args.notNullOrEmpty(getName(), "name");
        Args.notNullOrEmpty(getDisplayName(), "displayName");
        Args.notNull(configuration, "configuration");
//        List<Query> queries = configuration.getQueryList();
//        Args.notNullOrEmpty(queries, "Query list");
//        for (Query query : queries) {
//            Args.notNull(query, "query");
//        }
        Args.notNull(getSchedule(), "schedule");
    }
}
