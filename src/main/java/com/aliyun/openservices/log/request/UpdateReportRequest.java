package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class UpdateReportRequest extends JobRequest {

    private static final long serialVersionUID = 8354269775475956777L;

    private final Report report;

    public UpdateReportRequest(String project, Report report) {
        super(project);
        Args.notNull(report, "report");
        this.report = report;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.PUT;
    }

    @Override
    public Object getBody() {
        return report.makeJob();
    }
}
