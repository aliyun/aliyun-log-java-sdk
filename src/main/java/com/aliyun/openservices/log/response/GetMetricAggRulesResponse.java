package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.MetricAggRules;

import java.util.Map;

public class GetMetricAggRulesResponse extends Response {

    MetricAggRules metricAggRules;

    public GetMetricAggRulesResponse(Map<String, String> headers) {
        super(headers);
    }

    public MetricAggRules getMetricAggRules() {
        return metricAggRules;
    }

    public void setMetricAggRules(MetricAggRules metricAggRules) {
        this.metricAggRules = metricAggRules;
    }
}
