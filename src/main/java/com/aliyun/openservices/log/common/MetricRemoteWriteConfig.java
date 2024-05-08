package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author xzz
 */
public class MetricRemoteWriteConfig {

    @JSONField(name = "enable")
    private boolean enable;

    @JSONField(name = "history_interval")
    private int historyInterval;

    @JSONField(name = "future_interval")
    private int futureInterval;

    @JSONField(name = "replica_field")
    private String replicaField;

    @JSONField(name = "replica_timeout_seconds")
    private int replicaTimeoutSeconds;

    @JSONField(name = "shard_group_strategy_list")
    private ShardGroupStrategyList shardGroupStrategyList;

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


    public static class ShardGroupStrategyList {
        @JSONField(name = "strategies")
        private List<ShardGroupStrategy> strategies;

        @JSONField(name = "try_other_shard")
        private boolean tryOtherShard;

        @JSONField(name = "last_update_time")
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
        @JSONField(name = "metric_names")
        private List<String> metricNames;
        @JSONField(name = "hash_labels")
        private List<String> hashLabels;
        @JSONField(name = "shard_group_count")
        private int shardGroupCount;

        @JSONField(name = "priority")
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
