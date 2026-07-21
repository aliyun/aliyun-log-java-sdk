package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author xizongzheng.xzz
 */
public class MetricDownSamplingConfig {
    @SerializedName("base")
    private MetricDownSamplingStatus base;

    @SerializedName("downsampling")
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


    public static class MetricDownSamplingStatus {
        @SerializedName("create_time")
        private long createTime;
        @SerializedName("ttl")
        private int ttl;
        @SerializedName("resolution_seconds")
        private int resolutionSeconds;

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