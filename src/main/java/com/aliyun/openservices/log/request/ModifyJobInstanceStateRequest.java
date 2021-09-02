package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;
public class ModifyJobInstanceStateRequest extends JobInstancesRequest {
    private static final long serialVersionUID = -6812128854822612846L;
    public ModifyJobInstanceStateRequest(String project, String jobName, String instanceId, String state) {
        super(project, jobName, instanceId);
        if (Consts.SCHEDULED_SQL_RUNNING.equals(state)){
            SetParam(Consts.JOB_INSTANCES_STATE, Consts.SCHEDULED_SQL_RUNNING);
        }else {
            throw new IllegalArgumentException("Invalid state: "+state+
                    ", state must be RUNNING.");
        }
    }
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }
}