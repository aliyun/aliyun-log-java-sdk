package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        MetricRemoteWriteConfig that = (MetricRemoteWriteConfig) o;

        if (enable != that.enable) {return false;}
        if (historyInterval != that.historyInterval) {return false;}
        if (futureInterval != that.futureInterval) {return false;}
        if (replicaTimeoutSeconds != that.replicaTimeoutSeconds) {return false;}
        return replicaField != null ? replicaField.equals(that.replicaField) : that.replicaField == null;
    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + historyInterval;
        result = 31 * result + futureInterval;
        result = 31 * result + (replicaField != null ? replicaField.hashCode() : 0);
        result = 31 * result + replicaTimeoutSeconds;
        return result;
    }
}
