package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.JobType;

import java.io.Serializable;
import java.util.Map;

/**
 * ScheduledSQL list jobInstances Request
 */
public class ListJobInstancesRequest extends JobInstancesRequest implements Serializable {
    private static final long serialVersionUID = 1109698724031406237L;
    private String jobName;
    private JobType jobType;
    private String state;
    private long fromTime;
    private long toTime;
    private long offset = 0;
    private long size = 100;

    public ListJobInstancesRequest(String project, String jobName, long fromTime, long toTime) {
        super(project);
        this.jobName = jobName;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public ListJobInstancesRequest(String project, String jobName, long fromTime, long toTime, String state) {
        super(project);
        this.jobName = jobName;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.state = state;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    @Override
    public String getUri() {
        return Consts.JOB_URI + "/" + jobName + Consts.JOB_INSTANCES_URI;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (state != null && !state.isEmpty()) {
            SetParam(Consts.JOB_INSTANCES_STATE, state);
        }
        if (jobType == null) {
            jobType = JobType.SCHEDULED_SQL;
        }
        SetParam(Consts.JOB_TYPE, String.valueOf(jobType));
        SetParam(Consts.JOB_INSTANCES_START_TIME, String.valueOf(fromTime));
        SetParam(Consts.JOB_INSTANCES_END_TIME, String.valueOf(toTime));
        SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
        SetParam(Consts.CONST_SIZE, String.valueOf(size));
        return super.GetAllParams();
    }
}