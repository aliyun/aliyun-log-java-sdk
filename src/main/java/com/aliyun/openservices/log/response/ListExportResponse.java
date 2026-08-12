package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.common.Export;
import com.aliyun.openservices.log.internal.Unmarshaller;
import com.aliyun.openservices.log.internal.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListExportResponse extends ResponseList<Export> implements Serializable {
    private static final long serialVersionUID = -4082911383764624613L;

    public ListExportResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    @InternalApi
    public Unmarshaller<Export> unmarshaller() {
        return new Unmarshaller<Export>() {
            @Override
            public Export unmarshal(JSONArray value, int index) {
                Export export = new Export();
                export.fromJsonObject(value.getJSONObject(index));
                return export;
            }
        };
    }
}
