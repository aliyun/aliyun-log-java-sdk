package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Args;

import java.util.Map;

public class ListJobHistoryRequest extends Request {

    private String jobName;
    private Integer offset;
    private Integer size;

    public ListJobHistoryRequest(String project, String jobName, Integer offset, Integer size) {
        super(project);
        Args.notNullOrEmpty(jobName, "Job name");
        this.jobName = jobName;
        this.offset = offset;
        this.size = size;
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
    public Map<String, String> GetAllParams() {
        Map<String, String> urlParameters = super.GetAllParams();
        if (offset != null) {
            urlParameters.put(Consts.CONST_OFFSET, offset.toString());
        }
        if (size != null) {
            urlParameters.put(Consts.CONST_SIZE, size.toString());
        }
        return urlParameters;
    }
}
