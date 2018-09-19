package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class RetryPolicy implements Serializable {

    public enum RetryType {
        NoRetry,
        Linear,
        // Exponential,
    }

    @JSONField
    private RetryType type;

    @JSONField
    private Integer deltaBackoff;

    @JSONField
    private Integer maxAttempts;

    public RetryPolicy() {
        this.type = RetryType.NoRetry;
    }

    public RetryPolicy(Integer deltaBackoff, Integer maxAttempts) {
        this.deltaBackoff = deltaBackoff;
        this.maxAttempts = maxAttempts;
        this.type = RetryType.Linear;
    }

    public RetryType getType() {
        return type;
    }

    public void setType(RetryType type) {
        this.type = type;
    }

    public Integer getDeltaBackoff() {
        return deltaBackoff;
    }

    public void setDeltaBackoff(Integer deltaBackoff) {
        this.deltaBackoff = deltaBackoff;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetryPolicy that = (RetryPolicy) o;

        if (getType() != that.getType()) return false;
        if (getDeltaBackoff() != null ? !getDeltaBackoff().equals(that.getDeltaBackoff()) : that.getDeltaBackoff() != null)
            return false;
        return getMaxAttempts() != null ? getMaxAttempts().equals(that.getMaxAttempts()) : that.getMaxAttempts() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getDeltaBackoff() != null ? getDeltaBackoff().hashCode() : 0);
        result = 31 * result + (getMaxAttempts() != null ? getMaxAttempts().hashCode() : 0);
        return result;
    }
}
