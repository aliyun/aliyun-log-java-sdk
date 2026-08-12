package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;

import java.io.Serializable;
import java.util.List;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ResourceRecord implements Serializable, JsonSerializable, JsonDeserializable {
    private static final long serialVersionUID = -1184418783117426648L;
    private String value = null;
    private String tag = null;
    private String id = null;
    private long createTime = 0;
    private long lastModifyTime = 0;

    public ResourceRecord() {}

    public ResourceRecord(String value) {
        this.value = value;
    }
    
    public ResourceRecord(String id, String value) {
        this.id = id;
        this.value= value;
    }

    public ResourceRecord(String id, String tag, String value) {
        this.id = id;
        this.tag = tag;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    @InternalApi
    public JSONObject toJsonObject() throws LogException {
        JSONObject result = new JSONObject();
        if (tag != null) {
            result.put(Consts.RESOURCE_RECORD_TAG, tag);
        }
        if (id != null) {
            result.put(Consts.RESOURCE_RECORD_ID, id);
        }
        result.put(Consts.RESOURCE_RECORD_VALUE, getValue());
        return result;
    }

    public static String toJsonString(List<ResourceRecord> records) throws LogException {
        JSONObject result = new JSONObject();
        JSONArray recordArray = new JSONArray();
        for (ResourceRecord r: records) {
            recordArray.add(r.toJsonObject());
        }
        result.put("records", recordArray);
        return result.toString();
    }


    @InternalApi
    public void fromJsonObject(JSONObject dict) throws LogException {
        setValue(dict.getString(Consts.RESOURCE_RECORD_VALUE));

        if (dict.containsKey(Consts.RESOURCE_RECORD_TAG)) {
            tag = dict.getString(Consts.RESOURCE_RECORD_TAG);
        }

        if (dict.containsKey(Consts.RESOURCE_RECORD_ID)) {
            id = dict.getString(Consts.RESOURCE_RECORD_ID);
        }

        if (dict.containsKey(Consts.RESOURCE_CREATE_TIME)) {
            createTime = dict.getIntValue(Consts.RESOURCE_CREATE_TIME);
        }

        if (dict.containsKey(Consts.RESOURCE_LAST_MODIFY_TIME)) {
            lastModifyTime = dict.getIntValue(Consts.RESOURCE_LAST_MODIFY_TIME);
        }

        try {
            JSONObject.parseObject(dict.getString(Consts.RESOURCE_RECORD_VALUE));
        } catch (JSONException e) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "response record value is not valid json", e, e.getMessage());
        }
    }

    public void fromJsonString(String content) throws LogException {
        JSONObject dict = JSONObject.parseObject(content);
        fromJsonObject(dict);
    }

    public void checkForCreate() throws IllegalArgumentException {
        checkForValue();
    }

    public void checkForUpdate() throws IllegalArgumentException {
        checkForValue();
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id is null/empty");
        }
    }

    public void checkForUpsert() throws IllegalArgumentException {
        checkForCreate();
    }

    private void checkForValue() throws IllegalArgumentException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value is null/empty");
        }
        try {
            JSONObject.parseObject(value);
        } catch (JSONException e) {
            throw new IllegalArgumentException("record value not valid json");
        }
    }
}
