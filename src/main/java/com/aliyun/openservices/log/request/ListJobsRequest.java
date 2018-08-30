package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;

import java.util.Map;

public class ListJobsRequest extends Request {

    private Integer offset;
    private Integer size;

    public ListJobsRequest(String project, Integer offset, Integer size) {
        super(project);
        this.offset = offset;
        this.size = size;
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
