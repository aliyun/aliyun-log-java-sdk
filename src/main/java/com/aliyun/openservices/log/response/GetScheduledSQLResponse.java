package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.common.ScheduledSQL;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;
public class GetScheduledSQLResponse extends Response {
    private static final long serialVersionUID = 2635073458491900186L;
    private ScheduledSQL scheduledSQL;
    public ScheduledSQL getScheduledSQL() {
        return scheduledSQL;
    }
    public GetScheduledSQLResponse(Map<String, String> headers) {
        super(headers);
    }
    @InternalApi
    public void fromJsonObject(JSONObject value, String requestId) throws LogException {
        scheduledSQL = new ScheduledSQL();
        try {
            scheduledSQL.fromJsonObject(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Unable to read JSON model: " + ex.getMessage(), ex, requestId);
        }
    }
}