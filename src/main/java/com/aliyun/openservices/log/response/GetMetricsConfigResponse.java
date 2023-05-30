package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.MetricsConfig;
import com.aliyun.openservices.log.common.ProjectQuota;
import com.aliyun.openservices.log.exception.LogException;

import java.util.Map;

public class GetMetricsConfigResponse extends Response {
    private MetricsConfig metricsConfig;

    public GetMetricsConfigResponse(Map<String, String> headers) {
        super(headers);
    }

    public void fromJsonObject(JSONObject obj) throws LogException {
        try {
            metricsConfig = JSONObject.parseObject(obj.getString("metricsConfigDetail"), MetricsConfig.class);
        } catch (JSONException e) {
            throw new LogException("InvalidErrorResponse", e.getMessage(),
                    GetRequestId());
        }
    }

    public MetricsConfig getMetricsConfig() {
        return metricsConfig;
    }
}
