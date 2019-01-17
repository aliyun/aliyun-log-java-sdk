package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Report;
import net.sf.json.JSONObject;

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

    public void deserialize(JSONObject value) {
        report = new Report();
        report.deserialize(value);
    }
}
