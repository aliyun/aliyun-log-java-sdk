package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.common.ETLV2;
import com.aliyun.openservices.log.internal.Unmarshaller;

import java.io.Serializable;
import java.util.Map;

public class ListETLV2Response extends ResponseList<ETLV2> implements Serializable {

    private static final long serialVersionUID = -6158296540776923094L;

    public ListETLV2Response(Map<String, String> headers) {
        super(headers);
    }

    @Override
    @InternalApi
    public Unmarshaller<ETLV2> unmarshaller() {
        return new Unmarshaller<ETLV2>() {
            @Override
            public ETLV2 unmarshal(JSONArray value, int index) {
                ETLV2 etlv2 = new ETLV2();
                etlv2.fromJsonObject(value.getJSONObject(index));
                return etlv2;
            }
        };
    }
}
