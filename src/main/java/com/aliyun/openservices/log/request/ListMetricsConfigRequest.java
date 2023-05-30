package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListMetricsConfigRequest extends Request {
    public void setOffset(int offset) {
        SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
    }

    public void setSize(int size) {
        SetParam(Consts.CONST_SIZE, String.valueOf(size));
    }

    public void setFilter(String metricsNameFilter) {
        SetParam("metricStore", metricsNameFilter);
    }

    public ListMetricsConfigRequest(String project) {
        super(project);
    }

    public ListMetricsConfigRequest(String project, int offset, int size) {
        super(project);
        setOffset(offset);
        setSize(size);
    }

    public ListMetricsConfigRequest(String project, int offset, int size, String metricsNameFilter) {
        this(project, offset, size);
        setFilter(metricsNameFilter);
    }
}
