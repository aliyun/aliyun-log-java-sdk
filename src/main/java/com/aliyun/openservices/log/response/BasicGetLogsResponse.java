package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.QueriedLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicGetLogsResponse extends Response {

    // TODO change to List<QueriedLog>
    protected ArrayList<QueriedLog> logs;

    public BasicGetLogsResponse(Map<String, String> headers) {
        super(headers);
        this.logs = new ArrayList<QueriedLog>();
    }

    public List<QueriedLog> getLogs() {
        return logs;
    }

    public void setLogs(List<QueriedLog> logs) {
        this.logs = new ArrayList<QueriedLog>(logs);
    }

    public void addLog(QueriedLog log) {
        this.logs.add(log);
    }
}
