package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Job;
import com.aliyun.openservices.log.common.JobList;
import com.aliyun.openservices.log.util.Args;

import java.util.List;
import java.util.Map;

public class ListJobsResponse extends Response {

    private List<Job> jobList;
    private int total;
    private int count;

    public ListJobsResponse(Map<String, String> headers, JobList jobList) {
        super(headers);
        Args.notNull(jobList, "JobList");
        this.jobList = jobList.getJobList();
        this.total = jobList.getTotal();
        this.count = jobList.getCount();
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

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }
}
