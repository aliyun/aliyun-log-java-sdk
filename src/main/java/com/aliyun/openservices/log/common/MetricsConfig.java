package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author xizongzheng.xzz
 */
public class MetricsConfig implements Serializable {

    @JSONField(name = "query_cache_config")
    private MetricQueryCacheConfig queryCacheConfig;

    @JSONField(name = "parallel_config")
    private MetricParallelConfig parallelConfig;

    @JSONField(name = "downsampling_config")
    private MetricDownSamplingConfig downSamplingConfig;

    @JSONField(name = "pushdown_config")
    private MetricPushdownConfig pushdownConfig;

    @JSONField(name = "remote_write_config")
    private MetricRemoteWriteConfig remoteWriteConfig;

    public MetricsConfig(MetricDownSamplingConfig downSamplingConfig) {
        this.downSamplingConfig = downSamplingConfig;
    }

    public MetricsConfig(MetricParallelConfig parallelConfig) {
        this.parallelConfig = parallelConfig;
    }

    public MetricsConfig(MetricQueryCacheConfig queryCacheConfig) {
        this.queryCacheConfig = queryCacheConfig;
    }

    public MetricsConfig(MetricPushdownConfig pushdownConfig) {
        this.pushdownConfig = pushdownConfig;
    }

    public MetricsConfig(MetricRemoteWriteConfig remoteWriteConfig) {
        this.remoteWriteConfig = remoteWriteConfig;
    }

    public MetricsConfig(MetricParallelConfig parallelConfig, MetricQueryCacheConfig queryCacheConfig) {
        this.parallelConfig = parallelConfig;
        this.queryCacheConfig = queryCacheConfig;
    }

    public MetricsConfig(MetricQueryCacheConfig queryCacheConfig, MetricParallelConfig parallelConfig,
                         MetricDownSamplingConfig downSamplingConfig) {
        this.queryCacheConfig = queryCacheConfig;
        this.parallelConfig = parallelConfig;
        this.downSamplingConfig = downSamplingConfig;
    }

    public MetricsConfig(MetricQueryCacheConfig queryCacheConfig, MetricParallelConfig parallelConfig, MetricPushdownConfig pushdownConfig) {
        this.queryCacheConfig = queryCacheConfig;
        this.parallelConfig = parallelConfig;
        this.pushdownConfig = pushdownConfig;
    }

    public MetricsConfig(MetricQueryCacheConfig queryCacheConfig, MetricParallelConfig parallelConfig, MetricDownSamplingConfig downSamplingConfig, MetricPushdownConfig pushdownConfig) {
        this.queryCacheConfig = queryCacheConfig;
        this.parallelConfig = parallelConfig;
        this.downSamplingConfig = downSamplingConfig;
        this.pushdownConfig = pushdownConfig;

    }

    public MetricsConfig(MetricQueryCacheConfig queryCacheConfig, MetricParallelConfig parallelConfig, MetricDownSamplingConfig downSamplingConfig, MetricPushdownConfig pushdownConfig, MetricRemoteWriteConfig remoteWriteConfig) {
        this.queryCacheConfig = queryCacheConfig;
        this.parallelConfig = parallelConfig;
        this.downSamplingConfig = downSamplingConfig;
        this.pushdownConfig = pushdownConfig;
        this.remoteWriteConfig = remoteWriteConfig;
    }

    public MetricQueryCacheConfig getQueryCacheConfig() {
        return queryCacheConfig;
    }

    public void setQueryCacheConfig(MetricQueryCacheConfig queryCacheConfig) {
        this.queryCacheConfig = queryCacheConfig;
    }

    public MetricParallelConfig getParallelConfig() {
        return parallelConfig;
    }

    public void setParallelConfig(MetricParallelConfig parallelConfig) {
        this.parallelConfig = parallelConfig;
    }

    public MetricPushdownConfig getPushdownConfig() {
        return pushdownConfig;
    }

    public void setPushdownConfig(MetricPushdownConfig pushdownConfig) {
        this.pushdownConfig = pushdownConfig;
    }


    public MetricRemoteWriteConfig getRemoteWriteConfig() {
        return remoteWriteConfig;
    }

    public void setRemoteWriteConfig(MetricRemoteWriteConfig remoteWriteConfig) {
        this.remoteWriteConfig = remoteWriteConfig;
    }

    public MetricDownSamplingConfig getDownSamplingConfig() {
        return downSamplingConfig;
    }

    public void setDownSamplingConfig(MetricDownSamplingConfig downSamplingConfig) {
        this.downSamplingConfig = downSamplingConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        MetricsConfig that = (MetricsConfig)o;

        if (queryCacheConfig != null ? !queryCacheConfig.equals(that.queryCacheConfig)
                : that.queryCacheConfig != null) {
            return false;
        }
        if (parallelConfig != null ? !parallelConfig.equals(that.parallelConfig) : that.parallelConfig != null) {
            return false;
        }
        if (pushdownConfig != null ? !pushdownConfig.equals(that.pushdownConfig) : that.pushdownConfig != null){
            return false;
        }
        if (remoteWriteConfig != null ? !remoteWriteConfig.equals(that.remoteWriteConfig) : that.remoteWriteConfig != null){
            return false;
        }
        return downSamplingConfig != null ? downSamplingConfig.equals(that.downSamplingConfig)
                : that.downSamplingConfig == null;
    }

    @Override
    public int hashCode() {
        int result = queryCacheConfig != null ? queryCacheConfig.hashCode() : 0;
        result = 31 * result + (parallelConfig != null ? parallelConfig.hashCode() : 0);
        result = 31 * result + (pushdownConfig != null ? pushdownConfig.hashCode() : 0);
        result = 31 * result + (remoteWriteConfig != null ? remoteWriteConfig.hashCode() : 0);
        result = 31 * result + (downSamplingConfig != null ? downSamplingConfig.hashCode() : 0);
        return result;
    }
}
