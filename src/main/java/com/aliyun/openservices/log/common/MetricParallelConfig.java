package com.aliyun.openservices.log.common;

import com.google.gson.annotations.SerializedName;

/**
 * @author xizongzheng.xzz
 */
public class MetricParallelConfig {
    @SerializedName("enable")
    private boolean enable;

    @SerializedName("mode")
    private String mode;

    @SerializedName("time_piece_interval")
    private int timePieceInterval;

    @SerializedName("time_piece_count")
    private int timePieceCount;

    @SerializedName("parallel_count_per_host")
    private int parallelCountPerHost;

    @SerializedName("total_parallel_count")
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