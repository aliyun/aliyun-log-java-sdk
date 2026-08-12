package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GetExportResponse extends Response {
    private static final long serialVersionUID = 2635076478491900186L;

    private Export export;

    public Export getExport() {
        return export;
    }

    public GetExportResponse(Map<String, String> headers) {
        super(headers);
    }

    @InternalApi
    public void fromJsonObject(JSONObject value, String requestId) throws LogException {
        export = new Export();
        try {
            export.fromJsonObject(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Unable to read JSON model: " + ex.getMessage(), ex, requestId);
        }
    }
}
