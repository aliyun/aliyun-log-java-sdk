package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.http.client.HttpMethod;

/**
 * Request for deleting logs from a log store
 */
public class DeleteLogStoreLogsRequest extends BasicRequest {

    private static final long serialVersionUID = 2859364729401856234L;

    private String logstore;
    private int from;
    private int to;
    private String query;

    /**
     * Construct a delete log store logs request
     *
     * @param project  project name
     * @param logstore log store name
     * @param from     start time (unix timestamp)
     * @param to       end time (unix timestamp)
     * @param query    query condition for filtering logs to delete
     */
    public DeleteLogStoreLogsRequest(String project, String logstore, int from, int to, String query) {
        super(project);
        this.logstore = logstore;
        this.from = from;
        this.to = to;
        this.query = query;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return "/logstores/" + logstore + "/deletelogtasks";
    }

    @Override
    public Object getBody() {
        JSONObject body = new JSONObject();
        body.put("from", from);
        body.put("to", to);
        body.put("query", query);
        return body;
    }
}
