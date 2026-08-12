package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.internal.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListIngestionResponse extends ResponseList<Ingestion> implements Serializable {

    private static final long serialVersionUID = -772562389342255859L;

    public ListIngestionResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    @InternalApi
    public Unmarshaller<Ingestion> unmarshaller() {
        return new Unmarshaller<Ingestion>() {
            @Override
            public Ingestion unmarshal(JSONArray value, int index) {
                Ingestion ingestion = new Ingestion();
                ingestion.fromJsonObject(value.getJSONObject(index));
                return ingestion;
            }
        };
    }
}
