package com.aliyun.openservices.log.request;

public class DeleteExportRequest extends DeleteJobRequest {
    private static final long serialVersionUID = 1160723327362479726L;

    public DeleteExportRequest(String project, String jobName) {
        super(project, jobName);
    }
}
