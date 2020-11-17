package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.common.ETL;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class GetETLResponse extends Response {

    private static final long serialVersionUID = 889623903109968396L;

    private ETL etl;

    public GetETLResponse(Map<String, String> headers) {
        super(headers);
    }

    public ETL getEtl() {
        return etl;
    }

    public void setEtl(ETL etl) {
        this.etl = etl;
    }

    public void deserialize(JSONObject value, String requestId) throws LogException {
        etl = new ETL();
        try {
            etl.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}
