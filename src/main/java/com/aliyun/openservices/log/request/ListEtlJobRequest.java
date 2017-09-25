package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListEtlJobRequest extends Request {

    private static final long serialVersionUID = -2837220600311281949L;

    public ListEtlJobRequest(String project, int offset, int size) {
        super(project);
        SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
        SetParam(Consts.CONST_SIZE, String.valueOf(size));
    }
}
