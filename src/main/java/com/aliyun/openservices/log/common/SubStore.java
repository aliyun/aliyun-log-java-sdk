package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.util.ArrayList;
import java.util.List;

public class SubStore {
    private String name;
    private int ttl;
    private int sortedKeyCount;
    private int timeIndex;
    private List<SubStoreKey> keys;

    public SubStore() {
        super();
    }

    public SubStore(String name, int ttl, int sortedKeyCount, int timeIndex, List<SubStoreKey> keys) {
        this.name = name;
        this.ttl = ttl;
        this.sortedKeyCount = sortedKeyCount;
        this.timeIndex = timeIndex;
        this.keys = keys;
        if (!isValid()) {
            throw new IllegalArgumentException("SubStore is invalid");
        }
    }

    public boolean isValid() {
        if (this.sortedKeyCount <= 0 || this.sortedKeyCount >= this.keys.size()) {
            return false;
        }
        if (this.timeIndex >= this.keys.size() || this.timeIndex < this.sortedKeyCount) {
            return false;
        }
        if (this.ttl <= 0 || this.ttl > 3650) {
            return false;
        }
        for (int i = 0; i < this.keys.size(); i++) {
            if (!this.keys.get(i).isValid()) {
                return false;
            }
            if (i == this.timeIndex && !"long".equals(this.keys.get(i).getType())) {
                return false;
            }
            if (i < this.sortedKeyCount && "double".equals(this.keys.get(i).getType())) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getSortedKeyCount() {
        return sortedKeyCount;
    }

    public void setSortedKeyCount(int sortedKeyCount) {
        this.sortedKeyCount = sortedKeyCount;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public List<SubStoreKey> getKeys() {
        return keys;
    }

    public void setKeys(List<SubStoreKey> keys) {
        this.keys = keys;
    }

    public void fromJsonString(String subStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(subStoreString);
            fromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateSubStore", e.getMessage(), e, "");
        }
    }

    private void fromJsonObject(JSONObject dict) {
        this.setName(dict.getString("name"));
        this.setTtl(dict.getIntValue("ttl"));
        this.setSortedKeyCount(dict.getIntValue("sortedKeyCount"));
        this.setTimeIndex(dict.getIntValue("timeIndex"));

        if (dict.containsKey("keys")) {
            JSONArray keysDict = dict.getJSONArray("keys");
            keys = new ArrayList<SubStoreKey>();
            for (int i = 0; i < keysDict.size(); i++) {
                JSONObject keyDict = keysDict.getJSONObject(i);
                String keyName = keyDict.getString("name");
                String keyType = keyDict.getString("type");
                SubStoreKey subStoreKey = new SubStoreKey(keyName, keyType);
                keys.add(subStoreKey);
            }
        }
    }

    public String toRequestString() {
        return toRequestJson().toString();
    }


    public JSONObject toRequestJson() {
        JSONObject subStoreDict = new JSONObject();
        subStoreDict.put("name", getName());
        subStoreDict.put("ttl", getTtl());
        subStoreDict.put("sortedKeyCount", getSortedKeyCount());
        subStoreDict.put("timeIndex", getTimeIndex());

        JSONArray keysDict = new JSONArray();
        for (SubStoreKey key : getKeys()) {
            JSONObject keyDict = new JSONObject();
            keyDict.put("name", key.getName());
            keyDict.put("type", key.getType());
            keysDict.add(keyDict);
        }
        subStoreDict.put("keys", keysDict);
        return subStoreDict;
    }
}
