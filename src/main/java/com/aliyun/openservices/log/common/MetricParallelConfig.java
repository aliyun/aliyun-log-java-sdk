package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xizongzheng.xzz
 */
public class MetricParallelConfig {
    @JSONField(name = "enable")
    private boolean enable;

    @JSONField(name = "mode")
    private String mode;

    @JSONField(name = "time_piece_interval")
    private int timePieceInterval;

    @JSONField(name = "time_piece_count")
    private int timePieceCount;

    @JSONField(name = "parallel_count_per_host")
    private int parallelCountPerHost;

    @JSONField(name = "total_parallel_count")
    private int totalParallelCount;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getTimePieceInterval() {
        return timePieceInterval;
    }

    public void setTimePieceInterval(int timePieceInterval) {
        this.timePieceInterval = timePieceInterval;
    }

    public int getTimePieceCount() {
        return timePieceCount;
    }

    public void setTimePieceCount(int timePieceCount) {
        this.timePieceCount = timePieceCount;
    }

    public int getParallelCountPerHost() {
        return parallelCountPerHost;
    }

    public void setParallelCountPerHost(int parallelCountPerHost) {
        this.parallelCountPerHost = parallelCountPerHost;
    }

    public int getTotalParallelCount() {
        return totalParallelCount;
    }

    public void setTotalParallelCount(int totalParallelCount) {
        this.totalParallelCount = totalParallelCount;
    }

}