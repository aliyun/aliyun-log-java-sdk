package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.common.AuditJob;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.internal.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListAuditJobResponse extends ResponseList<AuditJob> implements Serializable {

    private static final long serialVersionUID = -953191421467248384L;

    public ListAuditJobResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    @InternalApi
    public Unmarshaller<AuditJob> unmarshaller() {
        return new Unmarshaller<AuditJob>() {
            @Override
            public AuditJob unmarshal(JSONArray value, int index) {
                AuditJob audit = new AuditJob();
                audit.fromJsonObject(value.getJSONObject(index));
                return audit;
            }
        };
    }
}
