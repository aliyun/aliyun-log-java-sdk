package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GetIngestionResponse extends Response {

    private static final long serialVersionUID = 7412302190899635806L;

    private Ingestion ingestion;

    public GetIngestionResponse(Map<String, String> headers) {
        super(headers);
    }

    public Ingestion getIngestion() {
        return ingestion;
    }

    @InternalApi
    public void fromJsonObject(JSONObject value, String requestId) throws LogException {
        ingestion = new Ingestion();
        try {
            ingestion.fromJsonObject(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Unable to read JSON model: " + ex.getMessage(), ex, requestId);
        }
    }
}
