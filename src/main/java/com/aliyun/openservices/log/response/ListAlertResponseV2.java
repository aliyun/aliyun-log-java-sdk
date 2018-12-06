package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.AlertV2;
import com.aliyun.openservices.log.internal.Unmarshaller;
import net.sf.json.JSONArray;

import java.util.Map;

public class ListAlertResponseV2 extends ResponseList<AlertV2> {

    public ListAlertResponseV2(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Unmarshaller<AlertV2> unmarshaller() {
        return new Unmarshaller<AlertV2>() {
            @Override
            public AlertV2 unmarshal(JSONArray value, int index) {
                AlertV2 alertV2 = new AlertV2();
                alertV2.deserialize(value.getJSONObject(index));
                return alertV2;
            }
        };
    }
}
