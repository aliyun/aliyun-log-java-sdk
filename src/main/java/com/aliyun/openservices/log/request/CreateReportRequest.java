package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.util.Args;

public class CreateReportRequest extends CreateJobRequest {

    private static final long serialVersionUID = -2062295245187846869L;

    public CreateReportRequest(String project, Report report) {
        super(project);
        Args.notNull(report, "report");
        setJob(report.makeJob());
    }
}
