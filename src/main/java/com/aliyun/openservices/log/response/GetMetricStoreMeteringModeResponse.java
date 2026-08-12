package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

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

    @InternalApi
    public void fromJsonObject(JSONObject asJson) {
        meteringMode = asJson.getString("meteringMode");
    }
}
