package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Job;
import net.sf.json.JSONObject;

import java.util.Map;

public class ListJobsResponse extends ResponseList<Job> {

    public ListJobsResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Job unmarshal(JSONObject value) {
        Job job = new Job();
        job.deserialize(value);
        return job;
    }
}
