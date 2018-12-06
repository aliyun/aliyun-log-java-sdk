package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.AlertV2;
import net.sf.json.JSONObject;

import java.util.Map;

public class GetAlertResponseV2 extends Response {

    private AlertV2 alert;

    public GetAlertResponseV2(Map<String, String> headers) {
        super(headers);
    }

    public AlertV2 getAlert() {
        return alert;
    }

    public void setAlert(AlertV2 alert) {
        this.alert = alert;
    }

    public void deserialize(JSONObject value) {
        alert = new AlertV2();
        alert.deserialize(value);
    }
}
