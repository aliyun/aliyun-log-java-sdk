package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.EtlJob;

public class CreateEtlJobRequest extends Request {

    private static final long serialVersionUID = -8781181608375525477L;
    protected EtlJob etlJob = null;

    public EtlJob getEtlJob() {
        return etlJob;
    }

    public void setEtlJob(EtlJob etlJob) {
        this.etlJob = etlJob;
    }

    public CreateEtlJobRequest(String project, EtlJob etlJob) {
        super(project);
        this.etlJob = etlJob;
    }
}
