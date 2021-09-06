package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListResourceRecordRequest extends RecordRequest {
    private Integer offset;
    private Integer size;
    private String tag;
    private String searchedValue;
    private String searchedJson;
    private String jsonPath;
    private String jsonPathValue;
    private List<String> recordIds = new ArrayList<String>();

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

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public ListResourceRecordRequest(String resourceName) {
        this(resourceName, null, 0, 100);
    }

    public ListResourceRecordRequest(String resourceName, String tag, int offset, int size) {
        super(resourceName);
        this.tag = tag;
        this.size = size;
        this.offset = offset;
    }

    @Override
    public Map<String, String> GetAllParams() {
        if (offset != null) {
            SetParam(Consts.CONST_OFFSET, offset.toString());
        }

        if (size != null) {
            SetParam(Consts.CONST_SIZE, size.toString());
        }

        if (tag != null && !tag.isEmpty()) {
            SetParam(Consts.RESOURCE_RECORD_TAG, tag);
        }

        if (recordIds != null && !recordIds.isEmpty()) {
            SetParam(Consts.RESOURCE_RECORD_IDS, Utils.join(",", recordIds));
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

        return super.GetAllParams();
    }

    public List<String> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<String> recordIds) {
        this.recordIds = recordIds;
    }
}
