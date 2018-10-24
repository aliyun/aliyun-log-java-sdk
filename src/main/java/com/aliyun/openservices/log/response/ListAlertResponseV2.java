package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.AlertV2;
import net.sf.json.JSONObject;

import java.util.Map;

public class ListAlertResponseV2 extends ResponseList<AlertV2> {

    public ListAlertResponseV2(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public AlertV2 unmarshal(JSONObject value) {
        AlertV2 alertV2 = new AlertV2();
        alertV2.deserialize(value);
        return alertV2;
    }

}
