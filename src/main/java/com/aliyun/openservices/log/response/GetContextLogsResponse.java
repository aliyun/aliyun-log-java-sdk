package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.QueriedLog;

import java.util.List;
import java.util.Map;

public class GetContextLogsResponse extends BasicGetLogsResponse {
    private boolean isCompleted = false;
    private int totalLines;
    private int backLines;
    private int forwardLines;

    public GetContextLogsResponse(Map<String, String> headers, com.alibaba.fastjson.JSONObject object) {
        super(headers);
        setProcessStatus(object.getString(Consts.CONST_RESULT_PROCESS));
        this.totalLines = object.getIntValue(Consts.CONST_TOTAL_LINES);
        this.backLines = object.getIntValue(Consts.CONST_BACK_LINES);
        this.forwardLines = object.getIntValue(Consts.CONST_FORWARD_LINES);
    }

    /**
     * @return the logs returned, the start log is included (at middle) if it is not empty.
     *   Each log has three extra fields: __tag__:__pack_id__, __pack_meta__ and __index_number__.
     *   Use the first two fields as parameters of getContextLogs to query more logs.
     *   The last one is the relative position of the start log whose __index_number__ is 0.
     */
    public List<QueriedLog> getLogs() {
        // The only reason to keep this is for the comments.
        return super.getLogs();
    }

    /**
     * Check if the result is completed, true at most time.
     *
     * @return true if the result is complete in the sls server
     */
    public boolean isCompleted() { return this.isCompleted; }

    /**
     * @return the number of lines returned.
     */
    public int getTotalLines() { return totalLines; }

    /**
     * @return the number of lines backward (before the start log).
     */
    public int getBackLines() { return backLines; }

    /**
     * @return the number of lines forward (after the start log).
     */
    public int getForwardLines() { return forwardLines; }

    /**
     * Users of getContextLogs can ignore following methods.
     */
    public void setProcessStatus(String processStatus) {
        if (processStatus.equals(Consts.CONST_RESULT_COMPLETE)) {
            isCompleted = true;
        } else {
            isCompleted = false;
        }
    }
}
