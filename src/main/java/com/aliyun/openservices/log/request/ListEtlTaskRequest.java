package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListEtlTaskRequest extends Request {
    private static final long serialVersionUID = 606469492743762805L;
    protected String etlJobName;
    protected long from;
    protected long to;
    protected int offset;
    protected int size;
    protected String status;

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public String getEtlJobName() {
        return etlJobName;
    }

    public ListEtlTaskRequest(String project, String etlJobName, long from, long to, int offset, int size, String status) {
        super(project);
        this.etlJobName = etlJobName;
        this.from = from;
        this.to = to;
        this.offset = offset;
        this.size = size;
        this.status = status;
        SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
        SetParam(Consts.CONST_SIZE, String.valueOf(size));
        SetParam(Consts.CONST_FROM, String.valueOf(from));
        SetParam(Consts.CONST_TO, String.valueOf(to));
        SetParam(Consts.CONST_STATUS, status);
    }
}
