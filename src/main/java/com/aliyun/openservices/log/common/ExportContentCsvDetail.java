package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

public class ExportContentCsvDetail extends ExportContentDetail {
    private String delimiter = ",";
    private String quote = "";
    private String lineFeed = "\n";
    @JSONField(name = "null")
    private String nullIdentifier = "";
    private boolean header = false;
    @JSONField(name = "columns")
    private ArrayList<String> storageColumns = new ArrayList<String>();

    public ExportContentCsvDetail() {}

    public ExportContentCsvDetail(String delimiter, String quote, String lineFeed, String nullIdentifier,
                                  boolean header, ArrayList<String> mStorageColumns) {
        this.delimiter = delimiter;
        this.quote = quote;
        this.lineFeed = lineFeed;
        this.nullIdentifier = nullIdentifier;
        this.header = header;
        this.storageColumns = mStorageColumns;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getLineFeed() {
        return lineFeed;
    }

    public void setLineFeed(String lineFeed) {
        this.lineFeed = lineFeed;
    }

    public String getNullIdentifier() {
        return nullIdentifier;
    }

    public void setNullIdentifier(String nullIdentifier) {
        this.nullIdentifier = nullIdentifier;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public ArrayList<String> getStorageColumns() {
        return storageColumns;
    }

    public void setStorageColumns(ArrayList<String> storageColumns) {
        this.storageColumns = storageColumns;
    }

    @Override
    public void deserialize(JSONObject value) {
        delimiter = value.getString("delimiter");
        quote = value.getString("quote");
        lineFeed = value.getString("lineFeed");
        nullIdentifier = value.getString("null");
        header = value.getBooleanValue("header");
        storageColumns = new ArrayList<String>();
        JSONArray arr = value.getJSONArray("columns");
        for (int i = 0; i < arr.size(); i++) {
            storageColumns.add(arr.getString(i));
        }
    }
}
