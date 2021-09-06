package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
public class GetJobInstanceRequest extends JobInstancesRequest {
    private static final long serialVersionUID = 9212386976679156583L;
    private String result;
    public GetJobInstanceRequest(String project, String jobName, String instanceId) {
        super(project, jobName, instanceId);
    }
    public GetJobInstanceRequest(String project, String jobName, String instanceId, String result) {
        super(project, jobName, instanceId);
        SetParam(Consts.JOB_INSTANCES_RESULT,result);
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
}