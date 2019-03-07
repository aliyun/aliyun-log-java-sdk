package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.http.client.HttpMethod;

import java.io.Serializable;
import java.util.Map;

public class ListJobRunsRequest extends JobRequest implements Serializable {

    private static final long serialVersionUID = -8035604951210400793L;

    private String jobName;
    private Integer offset;
    private Integer size;

    public ListJobRunsRequest(String project) {
        super(project);
    }

    public ListJobRunsRequest(String project, String jobName) {
        super(project);
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getUri() {
        return Consts.JOB_URI;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (jobName != null && !jobName.isEmpty()) {
            SetParam(Consts.JOB_NAME, jobName);
        }
        if (offset != null) {
            SetParam(Consts.CONST_OFFSET, offset.toString());
        }
        if (size != null) {
            SetParam(Consts.CONST_SIZE, size.toString());
        }
        return super.GetAllParams();
    }
}
