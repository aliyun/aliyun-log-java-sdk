package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Args;
import com.aliyun.openservices.log.util.Utils;

import java.util.Date;
import java.util.Map;

public class ListJobHistoryRequest extends Request {

    private String jobName;
    private Date startTime;
    private Date endTime;
    private Integer offset;
    private Integer size;

    public ListJobHistoryRequest(String project, String jobName, Date startTime, Date endTime) {
        super(project);
        Args.notNullOrEmpty(jobName, "Job name");
        this.jobName = jobName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
        Args.notNull(startTime, Consts.START_TIME);
        Args.notNull(endTime, Consts.END_TIME);
        urlParameters.put(Consts.START_TIME, String.valueOf(Utils.getTimestamp(startTime)));
        urlParameters.put(Consts.END_TIME, String.valueOf(Utils.getTimestamp(endTime)));
        if (offset != null) {
            urlParameters.put(Consts.CONST_OFFSET, offset.toString());
        }
        if (size != null) {
            urlParameters.put(Consts.CONST_SIZE, size.toString());
        }
        return urlParameters;
    }
}
