package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Job;
import net.sf.json.JSONObject;

import java.util.Map;

public class GetJobResponse extends Response {

    private Job job;

    public GetJobResponse(Map<String, String> headers) {
        super(headers);
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void deserialize(JSONObject value) {
        job = new Job();
        job.deserialize(value);
    }
}
