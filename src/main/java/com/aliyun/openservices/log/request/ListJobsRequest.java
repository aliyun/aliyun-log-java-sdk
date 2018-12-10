package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.JobType;
import com.aliyun.openservices.log.http.client.HttpMethod;

import java.util.Map;

public class ListJobsRequest extends JobRequest {

    private static final long serialVersionUID = -8772042308666861845L;

    /**
     * Job name for fuzzy matching
     */
    private String name;
    /**
     * Job type for filtering
     */
    private JobType type;
    /**
     * resourceProvider for searching
     */
    private String resourceProvider;
    private Integer offset;
    private Integer size;

    public ListJobsRequest(String project) {
        super(project);
    }

    public ListJobsRequest(String project, JobType type) {
        super(project);
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
        return Consts.JOB_URI;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public String getResourceProvider() {
        return resourceProvider;
    }

    public void setResourceProvider(String resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (name != null && !name.isEmpty()) {
            SetParam(Consts.JOB_NAME, name);
        }
        if (type != null) {
            SetParam(Consts.JOB_TYPE, type.toString());
        }
        if (resourceProvider != null && !resourceProvider.isEmpty()) {
            SetParam(Consts.RESOURCE_PROVIDER, resourceProvider);
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
