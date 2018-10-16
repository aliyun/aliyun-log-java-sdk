package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Utils;

import java.util.Date;
import java.util.Map;

public class ListJobHistoryRequest extends JobRequest {

    private String name;
    private JobType type;
    private Date startTime;
    private Date endTime;
    private Integer offset;
    private Integer size;

    public ListJobHistoryRequest(String project) {
        super(project);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String getUri() {
        return Consts.JOB_URI + "/" + name + "/history";
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

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (name != null && !name.isEmpty()) {
            SetParam(Consts.JOB_NAME, name);
        }
        if (type != null) {
            SetParam(Consts.JOB_TYPE, type.toString());
        }
        if (startTime != null) {
            SetParam(Consts.START_TIME, String.valueOf(Utils.getTimestamp(startTime)));
        }
        if (endTime != null) {
            SetParam(Consts.END_TIME, String.valueOf(Utils.getTimestamp(endTime)));
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
