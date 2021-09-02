package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ResourceRecord;

import java.util.Map;

public class GetResourceRecordResponse extends Response {

    private ResourceRecord record;

    public GetResourceRecordResponse(Map<String, String> headers, ResourceRecord record) {
        super(headers);
        this.record = record;
    }

    public ResourceRecord getRecord() {
        return record;
    }

    public void setRecord(ResourceRecord record) {
        this.record = record;
    }
}
