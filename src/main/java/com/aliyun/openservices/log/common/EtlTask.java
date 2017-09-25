package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class EtlTask implements Serializable {

    private static final long serialVersionUID = 5322866467996777994L;
    private String taskId;
    private String taskStatus;
    private long createTime;
    private long beginTime;
    private long finishTime;
    private long latestDataTime;
    private int retryTime;
    private String errorCode;
    private String errorMessage;
    private long ingestLines;
    private long ingestBytes;
    private long shipLines;
    private long shipBytes;
    private boolean enable;

    public String getTaskId() {
        return taskId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public long getLatestDataTime() {
        return latestDataTime;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public long getIngestLines() {
        return ingestLines;
    }

    public long getIngestBytes() {
        return ingestBytes;
    }

    public long getShipLines() {
        return shipLines;
    }

    public long getShipBytes() {
        return shipBytes;
    }

    public boolean getEnable() {
        return enable;
    }

    public EtlTask() {}

    public void fromJsonObject(JSONObject obj) throws LogException {
        try {
            this.taskId = obj.getString(Consts.ETL_TASK_ID);
            this.taskStatus = obj.getString(Consts.ETL_TASK_STATUS);
            this.createTime = obj.getInt(Consts.ETL_TASK_CREATE_TIME);
            this.beginTime = obj.getInt(Consts.ETL_TASK_BEGIN_TIME);
            this.finishTime = obj.getInt(Consts.ETL_TASK_FINISH_TIME);
            this.latestDataTime = obj.getInt(Consts.ETL_TASK_LATEST_DATA_TIME);
            this.retryTime = obj.getInt(Consts.ETL_TASK_RETRYT_TIME);
            this.errorCode = obj.getString(Consts.ETL_TASK_ERROR_CODE);
            this.errorMessage = obj.getString(Consts.ETL_TASK_ERROR_MESSAGE);
            this.ingestLines = obj.getLong(Consts.ETL_TASK_INGEST_LINES);
            this.ingestBytes = obj.getLong(Consts.ETL_TASK_INGEST_BYTES);
            this.shipLines = obj.getLong(Consts.ETL_TASK_SHIP_LINES);
            this.shipBytes = obj.getLong(Consts.ETL_TASK_SHIP_BYTES);
            this.enable = obj.getBoolean(Consts.ETL_ENABLE);
        } catch (JSONException e) {
            throw new LogException("ParseEtlTaskFail", e.getMessage(), e, "");
        }
    }

    public String toJsonString() {
        JSONObject taskObj = new JSONObject();
        taskObj.put(Consts.ETL_TASK_ID, this.taskId);
        taskObj.put(Consts.ETL_TASK_STATUS, this.taskStatus);
        taskObj.put(Consts.ETL_TASK_CREATE_TIME, this.createTime);
        taskObj.put(Consts.ETL_TASK_BEGIN_TIME, this.beginTime);
        taskObj.put(Consts.ETL_TASK_FINISH_TIME, this.finishTime);
        taskObj.put(Consts.ETL_TASK_LATEST_DATA_TIME, this.latestDataTime);
        taskObj.put(Consts.ETL_TASK_RETRYT_TIME, this.retryTime);
        taskObj.put(Consts.ETL_TASK_ERROR_CODE, this.errorCode);
        taskObj.put(Consts.ETL_TASK_ERROR_MESSAGE, this.errorMessage);
        taskObj.put(Consts.ETL_TASK_INGEST_LINES, this.ingestLines);
        taskObj.put(Consts.ETL_TASK_INGEST_BYTES, this.ingestBytes);
        taskObj.put(Consts.ETL_TASK_SHIP_LINES, this.shipLines);
        taskObj.put(Consts.ETL_TASK_SHIP_BYTES, this.shipBytes);
        taskObj.put(Consts.ETL_ENABLE, this.enable);
        return taskObj.toString();
    }
}
