package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.util.Args;

public class UpdateReportRequest extends UpdateJobRequest {

    private static final long serialVersionUID = 8354269775475956777L;

    private Report report;

    public UpdateReportRequest(String project, Report report) {
        super(project);
        Args.notNull(report, "report");
        this.report = report;
        setName(report.getName());
    }

    public Report getReport() {
        return report;
    }

    @Override
    public Object getBody() {
        return report;
    }
}
