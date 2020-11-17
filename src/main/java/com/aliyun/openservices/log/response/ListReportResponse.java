package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Report;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListReportResponse extends ResponseList<Report> implements Serializable {

    private static final long serialVersionUID = 8292912274110714070L;

    public ListReportResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Unmarshaller<Report> unmarshaller() {
        return new Unmarshaller<Report>() {
            @Override
            public Report unmarshal(JSONArray value, int index) {
                Report report = new Report();
                report.deserialize(value.getJSONObject(index));
                return report;
            }
        };
    }
}
