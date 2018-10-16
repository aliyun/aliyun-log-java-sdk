package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.AlertHistory;
import net.sf.json.JSONObject;

import java.util.Map;

public class ListAlertHistoryResponse extends ResponseList<AlertHistory> {


    public ListAlertHistoryResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public AlertHistory unmarshal(JSONObject value) {
        AlertHistory history = new AlertHistory();
        history.deserialize(value);
        return history;
    }
}
