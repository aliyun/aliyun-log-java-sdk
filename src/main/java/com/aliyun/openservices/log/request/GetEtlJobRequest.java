package com.aliyun.openservices.log.request;

public class GetEtlJobRequest extends Request {

    private static final long serialVersionUID = -3875063421201202696L;
    private String etlJobName;

    public String getEtlJobName() {
        return etlJobName;
    }

    public void setEtlJobName(String etlJobName) {
        this.etlJobName = etlJobName;
    }

    public GetEtlJobRequest(String project, String etlJobName) {

        super(project);
        this.etlJobName = etlJobName;
    }
}
