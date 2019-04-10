package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ETL;
import com.aliyun.openservices.log.internal.Unmarshaller;
import net.sf.json.JSONArray;

import java.io.Serializable;
import java.util.Map;

public class ListETLResponse extends ResponseList<ETL> implements Serializable {

    private static final long serialVersionUID = -6158296540776923094L;

    public ListETLResponse(Map<String, String> headers) {
        super(headers);
    }

    @Override
    public Unmarshaller<ETL> unmarshaller() {
        return new Unmarshaller<ETL>() {
            @Override
            public ETL unmarshal(JSONArray value, int index) {
                ETL etl = new ETL();
                etl.deserialize(value.getJSONObject(index));
                return etl;
            }
        };
    }
}
