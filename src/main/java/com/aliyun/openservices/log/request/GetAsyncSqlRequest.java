package com.aliyun.openservices.log.request;

import java.util.HashMap;
import java.util.Map;

public class GetAsyncSqlRequest extends Request {
    // Preserve compatibility with requests serialized before the logstore overload.
    private static final long serialVersionUID = -7007014235272158263L;

    private final String logstore;
    private final String queryId;
    private final int offset;
    private final int lines;

    public GetAsyncSqlRequest(String project, String queryId, int offset, int lines) {
        this(project, null, queryId, offset, lines);
    }

    /**
     * @param logstore optional logstore; null or empty omits the logstore parameter
     */
    public GetAsyncSqlRequest(String project, String logstore, String queryId, int offset, int lines) {
        super(project);
        this.logstore = logstore;
        this.queryId = queryId;
        this.offset = offset;
        this.lines = lines;
    }

    public String getLogstore() {
        return logstore;
    }

    public String getQueryId() {
        return queryId;
    }

    public int getOffset() {
        return offset;
    }

    public int getLines() {
        return lines;
    }

    @Override
    public Map<String, String> GetAllParams() {
        Map<String, String> params = new HashMap<>();
        if (logstore != null && !logstore.isEmpty()) {
            params.put("logstore", logstore);
        }
        params.put("offset", String.valueOf(offset));
        params.put("line", String.valueOf(lines));
        return params;
    }
}
