package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.EtlJob;

import java.util.Map;

public class GetEtlJobResponse extends Response {

    private static final long serialVersionUID = -1358283804315034924L;
    protected EtlJob etljob;

    public EtlJob getEtljob() {
        return etljob;
    }

    public void setEtljob(EtlJob etljob) {
        this.etljob = etljob;
    }

    public GetEtlJobResponse(Map<String, String> headers, EtlJob etlJob) {
        super(headers);
        this.etljob = etlJob;
    }
}
