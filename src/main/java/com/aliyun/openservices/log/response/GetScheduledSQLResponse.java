package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.common.ScheduledSQL;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import java.util.Map;
public class GetScheduledSQLResponse extends Response {
    private static final long serialVersionUID = 2635073458491900186L;
    private ScheduledSQL scheduledSQL;
    public ScheduledSQL getScheduledSQL() {
        return scheduledSQL;
    }
    public GetScheduledSQLResponse(Map<String, String> headers) {
        super(headers);
    }
    public void deserialize(JSONObject value, String requestId) throws LogException {
        scheduledSQL = new ScheduledSQL();
        try {
            scheduledSQL.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}