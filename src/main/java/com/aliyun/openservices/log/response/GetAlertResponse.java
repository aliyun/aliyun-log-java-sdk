package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.Alert;
import net.sf.json.JSONObject;

import java.util.Map;

public class GetAlertResponse extends Response {

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

    public void deserialize(JSONObject value) {
        alert = new Alert();
        alert.deserialize(value);
    }
}
