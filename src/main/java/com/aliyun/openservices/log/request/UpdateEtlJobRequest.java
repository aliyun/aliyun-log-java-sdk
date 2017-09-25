package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.EtlJob;

public class UpdateEtlJobRequest extends Request {

    private static final long serialVersionUID = 8542963214996939776L;
    protected EtlJob etlJob = null;

    public EtlJob getEtlJob() {
        return etlJob;
    }

    public void setEtlJob(EtlJob etlJob) {
        this.etlJob = etlJob;
    }

    public UpdateEtlJobRequest(String project, EtlJob etlJob) {
        super(project);
        this.etlJob = etlJob;
    }
}
