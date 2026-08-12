package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.internal.json.JSONObject;

/**
 * Request for deleting logs from a logstore using the DeleteLogs V2 API.
 */
public class DeleteLogsV2Request extends BasicRequest {

    private static final long serialVersionUID = 3229353747684854066L;

    private String logstore;
    private Integer from;
    private Integer to;
    private String query;
    private String rowId;

    /**
     * Construct a delete logs v2 request.
     *
     * @param project  project name
     * @param logstore logstore name
     */
    public DeleteLogsV2Request(String project, String logstore) {
        super(project);
        this.logstore = logstore;
    }

    /**
     * Construct a delete logs v2 request.
     *
     * @param project  project name
     * @param logstore logstore name
     * @param from     start time (unix timestamp)
     * @param to       end time (unix timestamp)
     * @param query    query condition for filtering logs
     * @param rowId    row id of the log
     */
    public DeleteLogsV2Request(String project, String logstore, Integer from, Integer to, String query, String rowId) {
        super(project);
        this.logstore = logstore;
        this.from = from;
        this.to = to;
        this.query = query;
        this.rowId = rowId;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return "/logstores/" + logstore + "/deletelogs";
    }

    @Override
    public Object getBody() {
        JSONObject body = new JSONObject();
        if (from != null) {
            body.put("from", from);
        }
        if (to != null) {
            body.put("to", to);
        }
        if (query != null) {
            body.put("query", query);
        }
        if (rowId != null) {
            body.put("rowId", rowId);
        }
        return body;
    }
}
