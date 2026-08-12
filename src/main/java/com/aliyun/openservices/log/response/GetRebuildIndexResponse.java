package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.RebuildIndex;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GetRebuildIndexResponse extends Response {

    private static final long serialVersionUID = 709919332677334375L;

    private RebuildIndex rebuildIndex;

    public GetRebuildIndexResponse(Map<String, String> headers) {
        super(headers);
    }

    public RebuildIndex getRebuildIndex() {
        return rebuildIndex;
    }

    @InternalApi
    public void fromJsonObject(JSONObject value, String requestId) throws LogException {
        rebuildIndex = new RebuildIndex();
        try {
            rebuildIndex.fromJsonObject(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Unable to read JSON model: " + ex.getMessage(), ex, requestId);
        }
    }
}
