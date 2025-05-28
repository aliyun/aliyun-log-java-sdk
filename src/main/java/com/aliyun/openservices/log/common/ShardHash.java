package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

public class ShardHash {
    private static final long serialVersionUID = 7411057377132011832L;
    private List<String> keys = new ArrayList<String>();
    private int maxHashCount = 1;

    public ShardHash() {
    }

    public ShardHash(int maxHashCount, List<String> keys) {
        this.maxHashCount = maxHashCount;
        this.keys = keys;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public int getMaxHashCount() {
        return maxHashCount;
    }

    public void setMaxHashCount(int maxHashCount) {
        this.maxHashCount = maxHashCount;
    }

    public JSONObject ToJsonObject() {
        JSONObject dict = new JSONObject();
        dict.put("maxHashCount", this.maxHashCount);
        JSONArray keysArray = new JSONArray();
        for (String key : keys) {
            keysArray.add(key);
        }
        dict.put("keys", keysArray);
        return dict;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        try {
            setMaxHashCount(dict.getIntValue("maxHashCount"));
            List<String> keys = new ArrayList<String>();
            JSONArray keysArray = dict.getJSONArray("keys");
            for (int i = 0; i < keysArray.size(); i++) {
                keys.add(keysArray.getString(i));
            }
            setKeys(keys);
        } catch (JSONException e) {
            throw new LogException("The shardHash config is invalid", e.getMessage(), e, "");
        }
    }

    public void FromJsonString(String logStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(logStoreString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("The shardHash config is invalid", e.getMessage(), e, "");
        }
    }
}
