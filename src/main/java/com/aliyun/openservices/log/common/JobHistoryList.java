package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JobHistoryList {

    @JSONField
    private Integer count;

    @JSONField
    private List<JobHistory> jobHistoryList;

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

    public void deserialize(JSONObject value) {
        count = value.getInt(Consts.CONST_COUNT);
        JSONArray jobsAsJson = value.getJSONArray(Consts.JOB_LIST);
        jobHistoryList = new ArrayList<JobHistory>(jobsAsJson.size());
        for (int i = 0; i < jobsAsJson.size(); i++) {
            JobHistory history = new JobHistory();
            history.deserialize(jobsAsJson.getJSONObject(i));
            jobHistoryList.add(history);
        }
    }
}
