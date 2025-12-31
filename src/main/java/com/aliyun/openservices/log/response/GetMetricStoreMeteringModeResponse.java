package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class GetMetricStoreMeteringModeResponse extends Response {

    private String meteringMode;

    public GetMetricStoreMeteringModeResponse(Map<String, String> headers) {
        super(headers);
    }

    public String getMeteringMode() {
        return meteringMode;
    }

    public void setMeteringMode(String meteringMode) {
        this.meteringMode = meteringMode;
    }

    public void deserializeFrom(JSONObject asJson) {
        meteringMode = asJson.getString("meteringMode");
    }
}

