package com.aliyun.openservices.log.common;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author xzz
 */
public class MetricRemoteWriteConfig {

    @SerializedName("enable")
    private boolean enable;

    @SerializedName("history_interval")
    private int historyInterval;

    @SerializedName("future_interval")
    private int futureInterval;

    @SerializedName("replica_field")
    private String replicaField;

    @SerializedName("replica_timeout_seconds")
    private int replicaTimeoutSeconds;

    @SerializedName("shard_group_strategy_list")
    private ShardGroupStrategyList shardGroupStrategyList;

    @SerializedName("trim_same_labels")
    private boolean trimSameLabels;

    @SerializedName("trim_empty_labels")
    private boolean trimEmptyLabels;

    @SerializedName("utf8_write_enable")
    private boolean utf8WriteEnable;

    public int getHistoryInterval() {
        return historyInterval;
    }

    public void setHistoryInterval(int historyInterval) {
        this.historyInterval = historyInterval;
    }

    public int getFutureInterval() {
        return futureInterval;
    }

    public void setFutureInterval(int futureInterval) {
        this.futureInterval = futureInterval;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getReplicaField() {
        return replicaField;
    }

    public void setReplicaField(String replicaField) {
        this.replicaField = replicaField;
    }

    public int getReplicaTimeoutSeconds() {
        return replicaTimeoutSeconds;
    }

    public void setReplicaTimeoutSeconds(int replicaTimeoutSeconds) {
        this.replicaTimeoutSeconds = replicaTimeoutSeconds;
    }

    public ShardGroupStrategyList getShardGroupStrategyList() {
        return shardGroupStrategyList;
    }

    public void setShardGroupStrategyList(ShardGroupStrategyList shardGroupStrategyList) {
        this.shardGroupStrategyList = shardGroupStrategyList;
    }

    public boolean isTrimSameLabels() {
        return trimSameLabels;
    }

    public void setTrimSameLabels(boolean trimSameLabels) {
        this.trimSameLabels = trimSameLabels;
    }

    public boolean isTrimEmptyLabels() {
        return trimEmptyLabels;
    }

    public void setTrimEmptyLabels(boolean trimEmptyLabels) {
        this.trimEmptyLabels = trimEmptyLabels;
    }

    public boolean isUtf8WriteEnable() {
        return utf8WriteEnable;
    }

    public void setUtf8WriteEnable(boolean utf8WriteEnable) {
        this.utf8WriteEnable = utf8WriteEnable;
    }


    public static class ShardGroupStrategyList {
        @SerializedName("strategies")
        private List<ShardGroupStrategy> strategies;

        @SerializedName("try_other_shard")
        private boolean tryOtherShard;

        @SerializedName("last_update_time")
        private int lastUpdateTime;


        public List<ShardGroupStrategy> getStrategies() {
            return strategies;
        }

        public void setStrategies(List<ShardGroupStrategy> strategies) {
            this.strategies = strategies;
        }

        public boolean isTryOtherShard() {
            return tryOtherShard;
        }

        public void setTryOtherShard(boolean tryOtherShard) {
            this.tryOtherShard = tryOtherShard;
        }

        public int getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(int lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }
    }

    public static class ShardGroupStrategy {
        @SerializedName("metric_names")
        private List<String> metricNames;
        @SerializedName("hash_labels")
        private List<String> hashLabels;
        @SerializedName("shard_group_count")
        private int shardGroupCount;

        @SerializedName("priority")
        private int priority;

        public List<String> getMetricNames() {
            return metricNames;
        }

        public void setMetricNames(List<String> metricNames) {
            this.metricNames = metricNames;
        }


        public int getShardGroupCount() {
            return shardGroupCount;
        }

        public void setShardGroupCount(int shardGroupCount) {
            this.shardGroupCount = shardGroupCount;
        }

        public List<String> getHashLabels() {
            return hashLabels;
        }

        public void setHashLabels(List<String> hashLabels) {
            this.hashLabels = hashLabels;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

    }

}
