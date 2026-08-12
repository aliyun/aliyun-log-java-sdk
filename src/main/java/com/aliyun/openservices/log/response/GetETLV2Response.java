package com.aliyun.openservices.log.response;


import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.ETLV2;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GetETLV2Response extends Response {

    private static final long serialVersionUID = 889623903109968396L;

    private ETLV2 etlv2;

    public GetETLV2Response(Map<String, String> headers) {
        super(headers);
    }

    public ETLV2 getEtl() {
        return etlv2;
    }

    public void setEtl(ETLV2 etlv2) {
        this.etlv2 = etlv2;
    }

    @InternalApi
    public void fromJsonObject(JSONObject value, String requestId) throws LogException {
        etlv2 = new ETLV2();
        try {
            etlv2.fromJsonObject(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "Unable to read JSON model: " + ex.getMessage(), ex, requestId);
        }
    }
}
