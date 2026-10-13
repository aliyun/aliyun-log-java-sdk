package com.aliyun.openservices.log.request;

public class DeleteAsyncSqlRequest extends Request {
    // Preserve compatibility with requests serialized before the logstore overload.
    private static final long serialVersionUID = 5259113902727799917L;

    private final String queryId;

    public DeleteAsyncSqlRequest(String project, String queryId) {
        this(project, null, queryId);
    }

    /**
     * @param logstore optional logstore; null or empty omits the logstore parameter
     */
    public DeleteAsyncSqlRequest(String project, String logstore, String queryId) {
        super(project);
        this.queryId = queryId;
        if (logstore != null && !logstore.isEmpty()) {
            SetParam("logstore", logstore);
        }
    }

    public String getQueryId() {
        return queryId;
    }
}
