package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class RebuildIndex extends AbstractJob implements Serializable {

    private static final long serialVersionUID = 949447748635414993L;

    private String status;

    private String executionDetails;

    @JSONField
    private RebuildIndexConfiguration configuration;

    public RebuildIndex() {
        setType(JobType.REBUILD_INDEX);
    }

    @Override
    public RebuildIndexConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RebuildIndexConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecutionDetails() {
        return executionDetails;
    }

    @Override
    public void deserialize(JSONObject value) {
        super.deserialize(value);
        status = JsonUtils.readOptionalString(value, "status");
        executionDetails = JsonUtils.readOptionalString(value, "executionDetails");
        configuration = new RebuildIndexConfiguration();
        configuration.deserialize(value.getJSONObject("configuration"));
    }
}
