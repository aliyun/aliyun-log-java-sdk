package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListExternalStoresRequest extends Request{
    private static final long serialVersionUID = 212203292047712113L;

    public ListExternalStoresRequest(String project, String storePattern, int offset, int size) {
        super(project);
        setPattern(storePattern);
        setOffset(offset);
        setSize(size);
    }

    public void setOffset(int offset)
    {
        SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
    }


    public void setSize(int size)
    {
        SetParam(Consts.CONST_SIZE,String.valueOf(size));
    }

    public void setPattern(String pattern) {
        SetParam(Consts.CONST_EXTERNAL_NAME, pattern);
    }
}
