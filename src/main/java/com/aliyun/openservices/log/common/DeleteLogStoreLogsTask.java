package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;

import java.io.Serializable;

/**
 * DeleteLogStoreLogsTask represents a soft delete task for log store logs
 */
public class DeleteLogStoreLogsTask implements Serializable {
    private static final long serialVersionUID = 6842917394758326187L;

    @JSONField
    private int progress;

    @JSONField
    private String taskId;

    @JSONField
    private String query;

    @JSONField
    private int errorCode;

    @JSONField
    private int from;

    @JSONField
    private int to;

    @JSONField
    private String errorMessage;

    public DeleteLogStoreLogsTask() {
    }

    public DeleteLogStoreLogsTask(String taskId, String query, int from, int to) {
        this.taskId = taskId;
        this.query = query;
        this.from = from;
        this.to = to;
        this.progress = 0;
        this.errorCode = 0;
        this.errorMessage = "";
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("progress", progress);
        json.put("taskId", taskId);
        json.put("query", query);
        json.put("errorCode", errorCode);
        json.put("from", from);
        json.put("to", to);
        json.put("errorMessage", errorMessage);
        return json;
    }

    public void fromJsonObject(JSONObject jsonObject) throws LogException {
        try {
            this.progress = jsonObject.getIntValue("progress");
            this.taskId = jsonObject.getString("taskId");
            this.query = jsonObject.getString("query");
            this.errorCode = jsonObject.getIntValue("errorCode");
            this.from = jsonObject.getIntValue("from");
            this.to = jsonObject.getIntValue("to");
            this.errorMessage = jsonObject.getString("errorMessage");
        } catch (Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Failed to parse DeleteLogStoreLogsTask from JSON: " + ex.getMessage(), ex, "");
        }
    }

    @Override
    public String toString() {
        return "DeleteLogStoreLogsTask{" +
                "progress=" + progress +
                ", taskId='" + taskId + '\'' +
                ", query='" + query + '\'' +
                ", errorCode=" + errorCode +
                ", from=" + from +
                ", to=" + to +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
