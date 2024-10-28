package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ResourceRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListNextResourceRecordResponse extends Response {
    protected int maxResults = 0;
    protected int total = 0;
    protected String nextToken;
    protected List<ResourceRecord> records = new ArrayList<ResourceRecord>();

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
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

    public String getNextToken() {
        return nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    public ListNextResourceRecordResponse (Map<String, String> headers, int maxResults, int total, String nextToken, List<ResourceRecord> records) {
        super(headers);
        setMaxResults(maxResults);
        setTotal(total);
        setNextToken(nextToken);
        this.records = records;
    }
}
