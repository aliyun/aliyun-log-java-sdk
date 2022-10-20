package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.util.Map;

public class GetProjectPolicyReponse extends Response {

    private String policyText;

    public GetProjectPolicyReponse(Map<String, String> headers, String policyText) {
        super(headers);
        this.policyText = policyText;
    }

    public String getPolicyText() {
        return policyText;
    }
}