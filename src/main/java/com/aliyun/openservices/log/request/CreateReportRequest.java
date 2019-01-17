package com.aliyun.openservices.log.request;


import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.util.Args;

public class CreateReportRequest extends JobRequest {
    
    private static final long serialVersionUID = -2062295245187846869L;

    private final Report report;

    public CreateReportRequest(String project, Report report) {
        super(project);
        Args.notNull(report, "report");
        this.report = report;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return Consts.JOB_URI;
    }

    @Override
    public Object getBody() {
        return report.makeJob();
    }
}
