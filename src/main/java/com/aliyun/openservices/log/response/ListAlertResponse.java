package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.internal.Unmarshaller;
import net.sf.json.JSONArray;

import java.util.Map;

public class ListAlertResponse extends ResponseList<Alert> {

    public ListAlertResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Unmarshaller<Alert> unmarshaller() {
        return new Unmarshaller<Alert>() {
            @Override
            public Alert unmarshal(JSONArray value, int index) {
                Alert alert = new Alert();
                alert.deserialize(value.getJSONObject(index));
                return alert;
            }
        };
    }
}
