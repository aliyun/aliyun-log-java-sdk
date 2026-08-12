package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.LogContent;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.http.client.HttpMethod;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.util.Map;

/**
 * Request for updating logs in a logstore.
 */
public class UpdateLogsRequest extends BasicRequest {

    private static final long serialVersionUID = 4838046559788772158L;

    private String logstore;
    private Integer from;
    private Integer to;
    private String query;
    private String rowId;
    private String updateMode;
    private String data;

    /**
     * Construct an update logs request.
     *
     * @param project  project name
     * @param logstore logstore name
     */
    public UpdateLogsRequest(String project, String logstore) {
        super(project);
        this.logstore = logstore;
    }

    /**
     * Construct an update logs request.
     *
     * @param project    project name
     * @param logstore   logstore name
     * @param from       start time (unix timestamp)
     * @param to         end time (unix timestamp)
     * @param query      query condition for filtering logs
     * @param rowId      row id of the log
     * @param updateMode update mode
     * @param data       update data
     */
    public UpdateLogsRequest(String project, String logstore, Integer from, Integer to, String query,
                             String rowId, String updateMode, String data) {
        super(project);
        this.logstore = logstore;
        this.from = from;
        this.to = to;
        this.query = query;
        this.rowId = rowId;
        this.updateMode = updateMode;
        this.data = data;
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

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDataFromMap(Map<String, ?> data) {
        this.data = data == null ? null : JsonUtils.serialize(data);
    }

    public void setLogItem(LogItem logItem) {
        this.data = toUpdateData(logItem);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getUri() {
        return "/logstores/" + logstore + "/updatelogs";
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
        if (updateMode != null) {
            body.put("updateMode", updateMode);
        }
        if (data != null) {
            body.put("data", data);
        }
        return body;
    }

    private static String toUpdateData(LogItem logItem) {
        if (logItem == null) {
            return null;
        }
        JSONObject data = new JSONObject();
        for (LogContent content : logItem.GetLogContents()) {
            data.put(content.GetKey(), content.GetValue());
        }
        return data.toString();
    }
}
