package com.aliyun.openservices.log.request;

public class DeleteEtlJobRequest extends Request {

    private static final long serialVersionUID = -1479644020840571938L;
    protected  String etlJobName;

    public void setEtlJobName(String etlJobName) {
        this.etlJobName = etlJobName;
    }

    public String getEtlJobName() {

        return etlJobName;
    }

    public DeleteEtlJobRequest(String project, String etlJobName) {
        super(project);
        this.etlJobName = etlJobName;
    }
}
