package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class RetryPolicy implements Serializable {

    @JSONField
    private RetryType type;

    @JSONField
    private int interval;

    @JSONField
    private int count;


    public RetryPolicy() {
        this.type = RetryType.None;
    }

    public RetryPolicy(int interval, int count) {
        this.type = RetryType.Fixed;
        this.interval = interval;
        this.count = count;
    }

    public RetryType getType() {
        return type;
    }

    public void setType(RetryType type) {
        this.type = type;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public enum RetryType {
        Fixed,
        None
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetryPolicy that = (RetryPolicy) o;

        if (getInterval() != that.getInterval()) return false;
        if (getCount() != that.getCount()) return false;
        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + getInterval();
        result = 31 * result + getCount();
        return result;
    }
}
