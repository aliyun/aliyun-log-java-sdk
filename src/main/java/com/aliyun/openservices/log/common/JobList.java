package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JobList {

    @JSONField
    private Integer total;

    @JSONField
    private Integer count;

    @JSONField
    private List<Job> jobList;

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

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public void deserialize(JSONObject value) {
        count = value.getInt(Consts.CONST_COUNT);
        total = value.getInt(Consts.CONST_TOTAL);
        JSONArray jobsAsJson = value.getJSONArray(Consts.JOB_LIST);
        jobList = new ArrayList<Job>(jobsAsJson.size());
        for (int i = 0; i < jobsAsJson.size(); i++) {
            Job job = new Job();
            job.deserialize(jobsAsJson.getJSONObject(i));
            jobList.add(job);
        }
    }
}
