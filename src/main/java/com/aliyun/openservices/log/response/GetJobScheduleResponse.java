package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.JobSchedule;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import net.sf.json.JSONObject;

import java.util.Map;

public class GetJobScheduleResponse extends Response {

    private static final long serialVersionUID = 4220425087831824694L;

    private JobSchedule jobSchedule;

    public GetJobScheduleResponse(Map<String, String> headers) {
        super(headers);
    }

    public JobSchedule getJobSchedule() {
        return jobSchedule;
    }

    public void deserialize(JSONObject value, String requestId) throws LogException {
        jobSchedule = new JobSchedule();
        try {
            jobSchedule.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}
