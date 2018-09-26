package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.JobHistory;
import com.aliyun.openservices.log.common.JobHistoryList;
import com.aliyun.openservices.log.util.Args;

import java.util.List;
import java.util.Map;

public class ListJobHistoryResponse extends Response {

    private int total;

    private int count;

    private List<JobHistory> jobHistoryList;

    public ListJobHistoryResponse(Map<String, String> headers, JobHistoryList jobHistoryList) {
        super(headers);
        Args.notNull(jobHistoryList, "jobHistoryList");
        this.jobHistoryList = jobHistoryList.getJobHistoryList();
        this.total = jobHistoryList.getTotal();
        this.count = jobHistoryList.getCount();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<JobHistory> getJobHistoryList() {
        return jobHistoryList;
    }

    public void setJobHistoryList(List<JobHistory> jobHistoryList) {
        this.jobHistoryList = jobHistoryList;
    }
}
