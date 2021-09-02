package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.JobInstance;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import java.util.Map;

/**
 * ScheduledSQL jobInstances Response
 */
public class GetJobInstanceResponse extends Response {
    private static final long serialVersionUID = 889623903109968396L;
    private JobInstance jobInstance;
    public GetJobInstanceResponse(Map<String, String> headers) {
        super(headers);
    }
    public JobInstance getJobInstance() {
        return jobInstance;
    }
    public void setJobInstance(JobInstance jobInstance) {
        this.jobInstance = jobInstance;
    }
    public void deserialize(JSONObject value, String requestId) throws LogException {
        jobInstance = new JobInstance();
        try {
            jobInstance.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}