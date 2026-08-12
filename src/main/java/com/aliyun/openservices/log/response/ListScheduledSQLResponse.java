package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.common.ScheduledSQL;
import com.aliyun.openservices.log.internal.Unmarshaller;
import java.io.Serializable;
import java.util.Map;
public class ListScheduledSQLResponse extends ResponseList<ScheduledSQL> implements Serializable {
    private static final long serialVersionUID = -4082922383764624613L;
    public ListScheduledSQLResponse(Map<String, String> headers) {
        super(headers);
    }
    @Override
    @InternalApi
    public Unmarshaller<ScheduledSQL> unmarshaller() {
        return new Unmarshaller<ScheduledSQL>() {
            @Override
            public ScheduledSQL unmarshal(JSONArray value, int index) {
                ScheduledSQL scheduledSQL = new ScheduledSQL();
                scheduledSQL.fromJsonObject(value.getJSONObject(index));
                return scheduledSQL;
            }
        };
    }
}
