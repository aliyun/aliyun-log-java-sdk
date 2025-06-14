package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListNextResourceRecordRequest extends RecordRequest {
    private String nextToken;
    private Integer maxResults;
    private String tag;
    private String searchedValue;
    private String searchedJson;
    private String jsonPath;
    private String jsonPathValue;
    private Boolean reverse;
    private Boolean includeSystemRecords;
    private List<String> recordIds = new ArrayList<String>();

    public String getNextToken() {
        return nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSearchedValue() {
        return searchedValue;
    }

    public void setSearchedValue(String searchedValue) {
        this.searchedValue = searchedValue;
    }

    public String getSearchedJson() {
        return searchedJson;
    }

    public void setSearchedJson(String searchedJson) {
        this.searchedJson = searchedJson;
    }

    public String getJsonPathValue() {
        return jsonPathValue;
    }

    public void setJsonPathValue(String jsonPathValue) {
        this.jsonPathValue = jsonPathValue;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    public Boolean getIncludeSystemRecords() {
        return includeSystemRecords;
    }

    public void setIncludeSystemRecords(Boolean includeSystemRecords) {
        this.includeSystemRecords = includeSystemRecords;
    }

    public List<String> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<String> recordIds) {
        this.recordIds = recordIds;
    }

    public ListNextResourceRecordRequest(String resourceName) {
        this(resourceName, null, null, 100);
    }

    public ListNextResourceRecordRequest(String resourceName, Boolean includeSystemRecords) {
        this(resourceName, null, null, 100, includeSystemRecords);
    }

    public ListNextResourceRecordRequest(String resourceName, String tag, String nextToken, int maxResults) {
        super(resourceName);
        this.tag = tag;
        this.nextToken = nextToken;
        this.maxResults = maxResults;
        this.reverse = false;
        this.includeSystemRecords = false;
    }

    public ListNextResourceRecordRequest(String resourceName, String tag, String nextToken, int maxResults, Boolean includeSystemRecords) {
        super(resourceName);
        this.tag = tag;
        this.nextToken = nextToken;
        this.maxResults = maxResults;
        this.reverse = false;
        this.includeSystemRecords = includeSystemRecords;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (nextToken != null) {
            SetParam(Consts.CONST_NEXT_TOKEN, nextToken);
        }

        if (maxResults != null) {
            SetParam(Consts.CONST_MAX_RESULTS, maxResults.toString());
        }

        if (tag != null && !tag.isEmpty()) {
            SetParam(Consts.RESOURCE_RECORD_TAG, tag);
        }

        if (searchedValue != null && !searchedValue.isEmpty()) {
            SetParam(Consts.RESOURCE_SEARCHED_VALUE, searchedValue);
        }

        if (searchedJson != null && !searchedJson.isEmpty()) {
            SetParam(Consts.RESOURCE_SEARCHED_JSON, searchedJson);
        }

        if (jsonPath != null && !jsonPath.isEmpty()) {
            SetParam(Consts.RESOURCE_JSON_PATH, jsonPath);
        }

        if (jsonPathValue != null && !jsonPathValue.isEmpty()) {
            SetParam(Consts.RESOURCE_JSON_PATH_VALUE, jsonPathValue);
        }

        if (reverse) {
            SetParam(Consts.RESOURCE_REVERSE, "true");
        }

        if (includeSystemRecords) {
            SetParam(Consts.RESOURCE_SYSTEM_RECORDS, "true");
        }

        if (recordIds != null && !recordIds.isEmpty()) {
            SetParam(Consts.RESOURCE_RECORD_IDS, Utils.join(",", recordIds));
        }

        return super.GetAllParams();
    }

}
