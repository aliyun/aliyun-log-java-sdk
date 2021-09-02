package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.AuditJob;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class GetAuditJobResponse extends Response {

    private static final long serialVersionUID = -2692561969145205579L;

    private AuditJob auditJob;

    public GetAuditJobResponse(Map<String, String> headers) {
        super(headers);
    }

    public AuditJob getAuditJob() {
        return auditJob;
    }

    public void deserialize(JSONObject value, String requestId) throws LogException {
        auditJob = new AuditJob();
        try {
            auditJob.deserialize(value);
        } catch (final Exception ex) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "Unable to deserialize JSON to model: " + ex.getMessage(), ex, requestId);
        }
    }
}
