package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.MetricAggRules;

import java.util.List;
import java.util.Map;

public class ListMetricAggRulesResponse extends Response {

    private static final long serialVersionUID = -6602153738113146519L;
    protected List<MetricAggRules> metricAggRules = null;
    protected int total;

    public ListMetricAggRulesResponse(Map<String, String> headers, int total) {
        super(headers);
        this.total = total;
    }

    public List<MetricAggRules> getMetricAggRules() {
        return metricAggRules;
    }

    public void setMetricAggRules(List<MetricAggRules> metricAggRules) {
        this.metricAggRules = metricAggRules;
    }
}
