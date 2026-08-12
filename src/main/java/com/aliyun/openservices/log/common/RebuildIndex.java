package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.util.JsonUtils;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.io.Serializable;
import com.aliyun.openservices.log.annotation.InternalApi;

public class RebuildIndex extends AbstractJob implements Serializable {

    private static final long serialVersionUID = 949447748635414993L;

    private String status;

    private String executionDetails;

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
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        put(value, "status", status);
        put(value, "executionDetails", executionDetails);
        return value;
    }

    public void fromJsonString(String rebuildIndexString) throws LogException {
        try {
            fromJsonObject(JSONObject.parseObject(rebuildIndexString));
        } catch (JSONException e) {
            throw new LogException("FailToGenerateRebuildIndex", e.getMessage(), e, "");
        }
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        super.fromJsonObject(value);
        status = JsonUtils.readOptionalString(value, "status");
        executionDetails = JsonUtils.readOptionalString(value, "executionDetails");
        configuration = new RebuildIndexConfiguration();
        configuration.fromJsonObject(value.getJSONObject("configuration"));
    }
}
