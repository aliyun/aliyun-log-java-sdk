package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.common.RebuildIndex;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.internal.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListRebuildIndexResponse extends ResponseList<RebuildIndex> implements Serializable {

    private static final long serialVersionUID = -1406759791382072342L;

    public ListRebuildIndexResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    @InternalApi
    public Unmarshaller<RebuildIndex> unmarshaller() {
        return new Unmarshaller<RebuildIndex>() {
            @Override
            public RebuildIndex unmarshal(JSONArray value, int index) {
                RebuildIndex item = new RebuildIndex();
                item.fromJsonObject(value.getJSONObject(index));
                return item;
            }
        };
    }
}
