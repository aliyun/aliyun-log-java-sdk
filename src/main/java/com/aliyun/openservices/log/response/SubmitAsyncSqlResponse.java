package com.aliyun.openservices.log.response;

import java.util.Map;

public class SubmitAsyncSqlResponse extends BaseAsyncSqlResponse {

    public SubmitAsyncSqlResponse(Map<String, String> headers,
                                  String queryId,
                                  String state,
                                  String errorCode,
                                  String errorMessage) {
        super(headers, queryId, state, errorCode, errorMessage);
    }
}
