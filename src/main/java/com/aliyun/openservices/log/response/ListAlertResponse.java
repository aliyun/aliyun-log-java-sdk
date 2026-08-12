package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.internal.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListAlertResponse extends ResponseList<Alert> implements Serializable {
    
    private static final long serialVersionUID = 5068019621076631746L;

    public ListAlertResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    @InternalApi
    public Unmarshaller<Alert> unmarshaller() {
        return new Unmarshaller<Alert>() {
            @Override
            public Alert unmarshal(JSONArray value, int index) {
                Alert alert = new Alert();
                alert.fromJsonObject(value.getJSONObject(index));
                return alert;
            }
        };
    }
}
