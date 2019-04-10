package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.JobScheduleType;
import com.aliyun.openservices.log.http.client.HttpMethod;

import java.util.Map;

public class ListJobSchedulesRequest extends BasicRequest {

    private static final long serialVersionUID = 4603013398882684317L;

    /**
     * Optional job name used to filter job schedules.
     */
    private String jobName;
    private JobScheduleType type;
    private Integer offset;
    private Integer size;

    public ListJobSchedulesRequest(String project) {
        super(project);
    }

    public ListJobSchedulesRequest(String project, String jobName) {
        super(project);
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobScheduleType getType() {
        return type;
    }

    public void setType(JobScheduleType type) {
        this.type = type;
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
        return Consts.JOB_SCHEDULE_URI;
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
        if (type != null) {
            SetParam(Consts.CONST_TYPE, type.toString());
        }
        return super.GetAllParams();
    }
}
