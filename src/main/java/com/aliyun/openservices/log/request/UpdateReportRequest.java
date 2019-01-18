package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.util.Args;

public class UpdateReportRequest extends UpdateJobRequest {

    private static final long serialVersionUID = 8354269775475956777L;

    public UpdateReportRequest(String project, Report report) {
        super(project);
        Args.notNull(report, "report");
        setJob(report.makeJob());
    }
}
