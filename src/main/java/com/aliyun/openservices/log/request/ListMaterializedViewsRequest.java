package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListMaterializedViewsRequest extends Request{

    public ListMaterializedViewsRequest(String project, String storePattern, int offset, int size) {
        super(project);
        setPattern(storePattern);
        setOffset(offset);
        setSize(size);
    }

    public void setOffset(int offset) {
        SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
    }

    public void setSize(int size) {
        SetParam(Consts.CONST_SIZE,String.valueOf(size));
    }

    public void setPattern(String pattern) {
        SetParam(Consts.CONST_EXTERNAL_NAME, pattern);
    }
}
