package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class JobHistoryList {

    @JSONField
    private Integer total;

    @JSONField
    private Integer count;

    @JSONField
    private List<JobHistory> jobHistoryList;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<JobHistory> getJobHistoryList() {
        return jobHistoryList;
    }

    public void setJobHistoryList(List<JobHistory> jobHistoryList) {
        this.jobHistoryList = jobHistoryList;
    }
}
