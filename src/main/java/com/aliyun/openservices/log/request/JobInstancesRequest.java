package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

/**
 * ScheduledSQL JobInstances Request
 */
public class JobInstancesRequest extends JobRequest {
    private String instanceId;
    private String jobName;
    public JobInstancesRequest(String project) {
        super(project);
    }
    public JobInstancesRequest(String project, String jobName, String instanceId) {
        super(project);
        Args.notNullOrEmpty(instanceId, "instanceId");
        this.instanceId = instanceId;
        this.jobName = jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobName() {
        return jobName;
    }
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
    @Override
    public String getUri() {
        return Consts.JOB_URI+"/"+jobName+Consts.JOB_INSTANCES_URI + "/" + instanceId;
    }
}