package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.MetricsConfig;
import com.aliyun.openservices.log.common.ProjectQuota;
import com.aliyun.openservices.log.exception.LogException;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GetMetricsConfigResponse extends Response {
    private MetricsConfig metricsConfig;

    public GetMetricsConfigResponse(Map<String, String> headers) {
        super(headers);
    }

    @InternalApi
    public void fromJsonObject(JSONObject obj) throws LogException {
        try {
            metricsConfig = JsonCodec.fromJson(obj.getString("metricsConfigDetail"), MetricsConfig.class);
        } catch (JSONException e) {
            throw new LogException("InvalidErrorResponse", e.getMessage(),
                    GetRequestId());
        }
    }

    public MetricsConfig getMetricsConfig() {
        return metricsConfig;
    }
}
