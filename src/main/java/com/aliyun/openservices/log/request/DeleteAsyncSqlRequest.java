package com.aliyun.openservices.log.request;

public class DeleteAsyncSqlRequest extends Request {
    private final String queryId;

    public DeleteAsyncSqlRequest(String project, String queryId) {
        super(project);
        this.queryId = queryId;
    }

    public String getQueryId() {
        return queryId;
    }
}
