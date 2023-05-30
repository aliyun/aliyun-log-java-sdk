package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class MetricsConfig implements Serializable {

    @JSONField(name = "downsampling_config")
    private MetricDownSamplingConfig downSamplingConfig;


    public MetricsConfig(MetricDownSamplingConfig downSamplingConfig) {
        this.downSamplingConfig = downSamplingConfig;
    }

    public MetricDownSamplingConfig getDownSamplingConfig() {
        return downSamplingConfig;
    }

    public void setDownSamplingConfig(MetricDownSamplingConfig downSamplingConfig) {
        this.downSamplingConfig = downSamplingConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricsConfig that = (MetricsConfig) o;

        return downSamplingConfig != null ? downSamplingConfig.equals(that.downSamplingConfig) : that.downSamplingConfig == null;
    }

    @Override
    public int hashCode() {
        return downSamplingConfig != null ? downSamplingConfig.hashCode() : 0;
    }
}
