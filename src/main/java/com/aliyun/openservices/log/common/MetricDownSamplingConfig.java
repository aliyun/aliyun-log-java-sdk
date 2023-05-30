package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class MetricDownSamplingConfig {
    @JSONField(name = "base")
    private MetricDownSamplingStatus base;

    @JSONField(name = "downsampling")
    private List<MetricDownSamplingStatus> downsampling = new ArrayList<MetricDownSamplingStatus>();

    public MetricDownSamplingStatus getBase() {
        return base;
    }
    public void setBase(MetricDownSamplingStatus base) {
        this.base = base;
    }
    public List<MetricDownSamplingStatus> getDownsampling() {
        return downsampling;
    }
    public void setDownsampling(List<MetricDownSamplingStatus> downsampling) {
        this.downsampling = downsampling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricDownSamplingConfig that = (MetricDownSamplingConfig) o;

        if (base != null ? !base.equals(that.base) : that.base != null) return false;
        if (downsampling == null) return that.downsampling == null;
        if (that.downsampling == null) return false;
        if (downsampling.size() != that.downsampling.size()) return false;
        for (int i = 0; i < downsampling.size(); i++) {
            if (!downsampling.get(i).equals(that.downsampling.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = base != null ? base.hashCode() : 0;
        result = 31 * result + (downsampling != null ? downsampling.hashCode() : 0);
        return result;
    }

    public static class MetricDownSamplingStatus {
        @JSONField(name = "create_time")
        private long createTime;
        @JSONField(name = "ttl")
        private int ttl;
        @JSONField(name = "resolution_seconds")
        private int resolutionSeconds;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MetricDownSamplingStatus that = (MetricDownSamplingStatus) o;

            if (ttl != that.ttl) return false;
            return resolutionSeconds == that.resolutionSeconds;
        }

        @Override
        public int hashCode() {
            int result = ttl;
            result = 31 * result + resolutionSeconds;
            return result;
        }

        public boolean isTtlDifferent(MetricDownSamplingStatus status) {
            return ttl != status.ttl;
        }
        public boolean isResolutionSecondsDifferent(MetricDownSamplingStatus status) {
            return resolutionSeconds != status.resolutionSeconds;
        }

        public long getCreateTime() {
            return createTime;
        }
        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
        public int getTtl() {
            return ttl;
        }
        public void setTtl(int ttl) {
            this.ttl = ttl;
        }
        public int getResolutionSeconds() {
            return resolutionSeconds;
        }
        public void setResolutionSeconds(int resolutionSeconds) {
            this.resolutionSeconds = resolutionSeconds;
        }
    }
}