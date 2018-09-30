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
        JSONArray list = value.getJSONArray(Consts.JOB_HISTORY_LIST);
        jobHistoryList = new ArrayList<JobHistory>(list.size());
        for (int i = 0; i < list.size(); i++) {
            JobHistory history = new JobHistory();
            history.deserialize(list.getJSONObject(i));
            jobHistoryList.add(history);
        }
    }
}
