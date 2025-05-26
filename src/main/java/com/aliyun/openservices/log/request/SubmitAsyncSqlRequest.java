package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SubmitAsyncSqlRequest extends Request {
    private final String logstore;
    private final String query;
    private final int begin;
    private final int end;
    private long maxRunMillis = 600_000;
    private boolean powerSqlEnabled = true;
    private Map<String, String> sessions = new HashMap<>();

    public SubmitAsyncSqlRequest(String project, String logstore, String query, int begin, int end) {
        super(project);
        this.logstore = logstore;
        this.query = query;
        this.begin = begin;
        this.end = end;
    }

    public String getQuery() {
        return query;
    }

    public String getLogstore() {
        return logstore;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public long getMaxRunMillis() {
        return maxRunMillis;
    }

    public void setMaxRunMillis(long maxRunMillis) {
        this.maxRunMillis = maxRunMillis;
    }

    public boolean isPowerSqlEnabled() {
        return powerSqlEnabled;
    }

    public void setPowerSqlEnabled(boolean powerSqlEnabled) {
        this.powerSqlEnabled = powerSqlEnabled;
    }

    public Map<String, String> getSessions() {
        return sessions;
    }

    public void addSession(String key, String value) {
        sessions.put(key, value);
    }

    public byte[] getRequestBody() {
        Map<String, Object> contents = new HashMap<>();
        contents.put("logstore", logstore);
        contents.put("query", query);
        contents.put("from", begin);
        contents.put("to", end);

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("maxRunTime", maxRunMillis);
        extensions.put("powerSql", powerSqlEnabled);
        if (!sessions.isEmpty()) {
            extensions.put("sessions", sessions);
        }
        contents.put("extensions", extensions);

        return JSON.toJSONString(contents).getBytes(StandardCharsets.UTF_8);
    }
}
