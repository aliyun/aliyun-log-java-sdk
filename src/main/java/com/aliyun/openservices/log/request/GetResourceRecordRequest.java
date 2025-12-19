package com.aliyun.openservices.log.request;

public class GetResourceRecordRequest extends RecordRequest {
    private static final long serialVersionUID = 3441625046836264998L;
    private String recordId;
    private Boolean includeSystemRecords;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Boolean getIncludeSystemRecords() {
        return includeSystemRecords;
    }

    public void setIncludeSystemRecords(Boolean includeSystemRecords) {
        this.includeSystemRecords = includeSystemRecords;
    }

    public GetResourceRecordRequest(String resourceName, String recordId) {
        super(resourceName);
        this.recordId = recordId;
        this.includeSystemRecords = false;
    }

    public GetResourceRecordRequest(String resourceName, String recordId, Boolean includeSystemRecords) {
        super(resourceName);
        this.recordId = recordId;
        this.includeSystemRecords = includeSystemRecords;
    }
}
