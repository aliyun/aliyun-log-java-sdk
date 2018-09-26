package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;

import java.util.Map;

public class ListJobsRequest extends Request {
    /**
     * Job name for  fuzzy matching
     */
    private String jobName;
    /**
     * Job type for filtering
     */
    private String jobType;
    /**
     * extraInfo for searching
     */
    private String extraInfo;
    private String resourceProdiver;
    private Integer offset;
    private Integer size;

    public ListJobsRequest(String project) {
        super(project);
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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public Map<String, String> GetAllParams() {
        Map<String, String> urlParameters = super.GetAllParams();
        if (jobName != null && !jobName.isEmpty()) {
            urlParameters.put(Consts.JOB_NAME, jobName);
        }
        if (jobType != null && !jobType.isEmpty()) {
            urlParameters.put(Consts.JOB_TYPE, jobType);
        }
        if (extraInfo != null && !extraInfo.isEmpty()) {
            urlParameters.put(Consts.EXTRA_INFO, extraInfo);
        }
        if (offset != null) {
            urlParameters.put(Consts.CONST_OFFSET, offset.toString());
        }
        if (size != null) {
            urlParameters.put(Consts.CONST_SIZE, size.toString());
        }
        return urlParameters;
    }
}
