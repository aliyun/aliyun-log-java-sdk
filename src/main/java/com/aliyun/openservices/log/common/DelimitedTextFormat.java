package com.aliyun.openservices.log.common;


import com.aliyun.openservices.log.util.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DelimitedTextFormat extends StructuredDataFormat {

    private List<String> fieldNames;
    private String fieldDelimiter;
    private String quoteChar;
    private String escapeChar;
    private Integer skipLeadingRows = 0;
    private Integer maxLines = -1;
    private boolean firstRowAsHeader = false;

    public DelimitedTextFormat() {
        super("DelimitedText");
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public String getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }

    public Integer getSkipLeadingRows() {
        return skipLeadingRows;
    }

    public void setSkipLeadingRows(Integer skipLeadingRows) {
        this.skipLeadingRows = skipLeadingRows;
    }

    public Integer getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(Integer maxLines) {
        this.maxLines = maxLines;
    }

    public boolean getFirstRowAsHeader() {
        return firstRowAsHeader;
    }

    public void setFirstRowAsHeader(boolean firstRowAsHeader) {
        this.firstRowAsHeader = firstRowAsHeader;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        fieldDelimiter = JsonUtils.readOptionalString(jsonObject, "fieldDelimiter");
        quoteChar = JsonUtils.readOptionalString(jsonObject, "quoteChar");
        escapeChar = JsonUtils.readOptionalString(jsonObject, "escapeChar");
        if (jsonObject.containsKey("skipLeadingRows")) {
            skipLeadingRows = jsonObject.getIntValue("skipLeadingRows");
        }
        if (jsonObject.containsKey("maxLines")) {
            maxLines = jsonObject.getIntValue("maxLines");
        }
        JSONArray array = jsonObject.getJSONArray("fieldNames");
        if (array != null) {
            fieldNames = new ArrayList<String>(array.size());
            for (int i = 0; i < array.size(); i++) {
                fieldNames.add(array.getString(i));
            }
        }
        firstRowAsHeader = JsonUtils.readBool(jsonObject, "firstRowAsHeader", false);
    }
}
