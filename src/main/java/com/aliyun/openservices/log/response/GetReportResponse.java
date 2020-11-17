package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class GetReportResponse extends Response {

    private static final long serialVersionUID = 3039200816847354835L;

    private Report report;

    public GetReportResponse(Map<String, String> headers) {
        super(headers);
    }

    public Report getReport() {
        return report;
    }

    public void deserialize(JSONObject value, final String requestId) throws LogException {
        report = new Report();
        try {
            report.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}
