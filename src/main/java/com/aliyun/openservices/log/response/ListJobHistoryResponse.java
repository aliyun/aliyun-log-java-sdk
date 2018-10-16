package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.JobHistory;
import net.sf.json.JSONObject;

import java.util.Map;

public class ListJobHistoryResponse extends ResponseList<JobHistory> {

    public ListJobHistoryResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public JobHistory unmarshal(JSONObject value) {
        JobHistory jobHistory = new JobHistory();
        jobHistory.deserialize(value);
        return jobHistory;
    }
}
