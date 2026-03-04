package com.aliyun.openservices.log.request;

import java.util.HashMap;
import java.util.Map;

public class DeleteAsyncSqlRequest extends Request {
    private final String logstore;
    private final String queryId;

    public DeleteAsyncSqlRequest(String project, String logstore, String queryId) {
        super(project);
        this.logstore = logstore;
        this.queryId = queryId;
    }

    public String getQueryId() {
        return queryId;
    }

    @Override
    public Map<String, String> GetAllParams() {
        Map<String, String> params = new HashMap<>();
        params.put("logstore", logstore);
        return params;
    }
}
