package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.Resource;
import com.aliyun.openservices.log.common.ResourceRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListResourceRecordResponse extends Response {
    protected int count = 0;
    protected int total = 0;
    protected List<ResourceRecord> records = new ArrayList<ResourceRecord>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ResourceRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ResourceRecord> records) {
        this.records = records;
    }

    public ListResourceRecordResponse(Map<String, String> headers, int count, int total, List<ResourceRecord> records) {
        super(headers);
        setCount(count);
        setTotal(total);
        this.records = records;
    }
}
