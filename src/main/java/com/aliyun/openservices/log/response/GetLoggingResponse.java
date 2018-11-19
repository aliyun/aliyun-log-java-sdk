package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Logging;
import net.sf.json.JSONObject;

import java.util.Map;

public class GetLoggingResponse extends Response {

    private Logging logging;

    /**
     * Construct a new {@link GetLoggingResponse} instance.
     *
     * @param headers The response headers.
     */
    public GetLoggingResponse(Map<String, String> headers) {
        super(headers);
    }

    public Logging getLogging() {
        return logging;
    }

    public void deserialize(JSONObject value) {
        logging = new Logging();
        logging.deserialize(value);
    }
}
