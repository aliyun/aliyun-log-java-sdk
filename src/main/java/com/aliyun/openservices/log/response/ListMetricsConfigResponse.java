package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.common.MetricsConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMetricsConfigResponse extends Response {

    private List<MetricsConfigWrap> metricsConfigList;

    public ListMetricsConfigResponse(Map<String, String> headers) {
        super(headers);
    }

    public ListMetricsConfigResponse(Map<String, String> headers, List<MetricsConfigWrap> metricsConfigList) {
        super(headers);
        this.metricsConfigList = metricsConfigList;
    }

    public void fromJSON(JSONObject object) {
        metricsConfigList = new ArrayList<MetricsConfigWrap>();
        if (object == null) {
            return;
        }
        JSONArray array = object.getJSONArray("metricsConfig");
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            String metricStore = jsonObject.getString("metricStore");
            MetricsConfig metricsConfig = JSONObject.parseObject(jsonObject.getString("metricsConfigDetail"), MetricsConfig.class);
            MetricsConfigWrap metricsConfigWrap = new MetricsConfigWrap(metricStore, metricsConfig);
            metricsConfigList.add(metricsConfigWrap);
        }
    }

    public List<MetricsConfigWrap> getMetricsConfigList() {
        return metricsConfigList;
    }

    public void setMetricsConfigList(List<MetricsConfigWrap> metricsConfigs) {
        this.metricsConfigList = metricsConfigs;
    }

    public static class MetricsConfigWrap {
        @JSONField
        private String metricStore;
        @JSONField
        private MetricsConfig metricsConfigDetail;

        public MetricsConfigWrap(String metricStore, MetricsConfig metricsConfig) {
            this.metricStore = metricStore;
            this.metricsConfigDetail = metricsConfig;
        }

        public String getMetricStore() {
            return metricStore;
        }

        public void setMetricStore(String metricStore) {
            this.metricStore = metricStore;
        }

        public MetricsConfig getMetricsConfig() {
            return metricsConfigDetail;
        }

        public void setMetricsConfig(MetricsConfig metricsConfig) {
            this.metricsConfigDetail = metricsConfig;
        }
    }

}