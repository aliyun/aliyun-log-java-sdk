package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.List;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.google.gson.annotations.SerializedName;

/**
 * @author xizongzheng.xzz
 */
public class MetricsConfig implements Serializable, JsonSerializable, JsonDeserializable {

    @SerializedName("query_cache_config")
    private MetricQueryCacheConfig queryCacheConfig;

    @SerializedName("parallel_config")
    private MetricParallelConfig parallelConfig;

    @SerializedName("downsampling_config")
    private MetricDownSamplingConfig downSamplingConfig;

    @SerializedName("pushdown_config")
    private MetricPushdownConfig pushdownConfig;

    @SerializedName("remote_write_config")
    private MetricRemoteWriteConfig remoteWriteConfig;

    @SerializedName("store_view_routing_config")
    private List<MetricStoreViewRoutingConfig> storeViewRoutingConfigs;

    public MetricsConfig() {
    }

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

    public MetricsConfig(MetricQueryCacheConfig queryCacheConfig, MetricParallelConfig parallelConfig, MetricDownSamplingConfig downSamplingConfig, MetricPushdownConfig pushdownConfig, MetricRemoteWriteConfig remoteWriteConfig, List<MetricStoreViewRoutingConfig> storeViewRoutingConfigs) {
        this.queryCacheConfig = queryCacheConfig;
        this.parallelConfig = parallelConfig;
        this.downSamplingConfig = downSamplingConfig;
        this.pushdownConfig = pushdownConfig;
        this.remoteWriteConfig = remoteWriteConfig;
        this.storeViewRoutingConfigs = storeViewRoutingConfigs;
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

    public List<MetricStoreViewRoutingConfig> getStoreViewRoutingConfigs() {
        return storeViewRoutingConfigs;
    }

    public void setStoreViewRoutingConfigs(List<MetricStoreViewRoutingConfig> storeViewRoutingConfigs) {
        this.storeViewRoutingConfigs = storeViewRoutingConfigs;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        return JsonCodec.toJsonObject(this);
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        MetricsConfig parsed = JsonCodec.fromJson(value, MetricsConfig.class);
        queryCacheConfig = parsed.queryCacheConfig;
        parallelConfig = parsed.parallelConfig;
        downSamplingConfig = parsed.downSamplingConfig;
        pushdownConfig = parsed.pushdownConfig;
        remoteWriteConfig = parsed.remoteWriteConfig;
        storeViewRoutingConfigs = parsed.storeViewRoutingConfigs;
    }
}
