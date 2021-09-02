package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class GetAlertResponse extends Response {

    private static final long serialVersionUID = 889623903109968396L;

    private Alert alert;

    public GetAlertResponse(Map<String, String> headers) {
        super(headers);
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public void deserialize(JSONObject value, String requestId) throws LogException {
        alert = new Alert();
        try {
            alert.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}
