package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Ingestion;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import net.sf.json.JSONObject;

import java.util.Map;

public class GetIngestionResponse extends Response {

    private static final long serialVersionUID = 7412302190899635806L;

    private Ingestion ingestion;

    public GetIngestionResponse(Map<String, String> headers) {
        super(headers);
    }

    public Ingestion getIngestion() {
        return ingestion;
    }

    public void deserialize(JSONObject value, String requestId) throws LogException {
        ingestion = new Ingestion();
        try {
            ingestion.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}
