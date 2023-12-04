package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xzz
 */
public class MetricRemoteWriteConfig {

    @JSONField(name = "history_interval")
    private int historyInterval;

    @JSONField(name = "future_interval")
    private int futureInterval;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        MetricRemoteWriteConfig that = (MetricRemoteWriteConfig) o;

        if (historyInterval != that.historyInterval) {return false;}
        return futureInterval == that.futureInterval;
    }

    @Override
    public int hashCode() {
        int result = historyInterval;
        result = 31 * result + futureInterval;
        return result;
    }
}
