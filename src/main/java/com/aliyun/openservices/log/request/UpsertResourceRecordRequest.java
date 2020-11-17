package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.ResourceRecord;

import java.util.Collections;
import java.util.List;


public class UpsertResourceRecordRequest extends RecordRequest {
    private List<ResourceRecord> records;

    public UpsertResourceRecordRequest(String resourceName, ResourceRecord record) {
        this(resourceName, Collections.singletonList(record));
    }

    public UpsertResourceRecordRequest(String resourceName, List<ResourceRecord> records) {
        super(resourceName);
        this.records = records;
    }

    public String getPostBody() {
        JSONObject result = new JSONObject();
        JSONArray encodedRecords = new JSONArray();
        encodedRecords.addAll(records);
        result.put(Consts.RESOURCE_RECORDS, encodedRecords);
        return result.toString();
    }

    public List<ResourceRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ResourceRecord> records) {
        this.records = records;
    }
}
