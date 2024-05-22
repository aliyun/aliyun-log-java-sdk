package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xizongzheng.xzz
 */
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


    public static class MetricDownSamplingStatus {
        @JSONField(name = "create_time")
        private long createTime;
        @JSONField(name = "ttl")
        private int ttl;
        @JSONField(name = "resolution_seconds")
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