package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListResourceRequest extends ResourceRequest {
    private Integer offset;
    private Integer size;
    private String type;
    private List<String> resourceNames = new ArrayList<String>();

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<String> getResourceNames() {
        return resourceNames;
    }

    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
    }

    public ListResourceRequest () {
        this(null, 0, 100);
    }

    public ListResourceRequest(String type, int offset, int size) {
        this.type = type;
        this.size = size;
        this.offset = offset;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (offset != null) {
            SetParam(Consts.CONST_OFFSET, offset.toString());
        }

        if (size != null) {
            SetParam(Consts.CONST_SIZE, size.toString());
        }

        if (type != null && !type.isEmpty()) {
            SetParam(Consts.RESOURCE_TYPE, type);
        }

        if (resourceNames != null && !resourceNames.isEmpty()) {
            SetParam(Consts.RESOURCE_NAMES, Utils.join(",", resourceNames));
        }
        return super.GetAllParams();
    }
}
