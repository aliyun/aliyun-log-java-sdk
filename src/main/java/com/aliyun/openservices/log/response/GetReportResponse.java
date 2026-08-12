package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GetReportResponse extends Response {

    private static final long serialVersionUID = 3039200816847354835L;

    private Report report;

    public GetReportResponse(Map<String, String> headers) {
        super(headers);
    }

    public Report getReport() {
        return report;
    }

    @InternalApi
    public void fromJsonObject(JSONObject value, final String requestId) throws LogException {
        report = new Report();
        try {
            report.fromJsonObject(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "Unable to read JSON model: " + ex.getMessage(), ex, requestId);
        }
    }
}
