package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

public class HashConfig implements Serializable {
    private static final long serialVersionUID = 7401057377332043832L;
    private List<String> keys = new ArrayList<String>();
    private int maxHashAttempts = 1;
    private ShardGroup shardGroup = null;

    public HashConfig() {
    }

    public HashConfig(int maxHashAttempts, List<String> keys) {
        this(maxHashAttempts, keys, null);
    }

    public HashConfig(int maxHashAttempts, List<String> keys, ShardGroup shardGroup) {
        this.shardGroup = shardGroup;
        this.maxHashAttempts = maxHashAttempts;
        this.keys = keys;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public int getMaxHashAttempts() {
        return maxHashAttempts;
    }

    public void setMaxHashAttempts(int maxHashAttempts) {
        this.maxHashAttempts = maxHashAttempts;
    }

    public ShardGroup getShardGroup() {
        return shardGroup;
    }

    public void setShardGroup(ShardGroup shardGroup) {
        this.shardGroup = shardGroup;
    }

    public JSONObject ToJsonObject() {
        JSONObject dict = new JSONObject();
        dict.put("maxHashAttempts", this.maxHashAttempts);
        JSONArray keysArray = new JSONArray();
        for (String key : keys) {
            keysArray.add(key);
        }
        dict.put("keys", keysArray);
        if (shardGroup != null) {
            dict.put("shardGroup", shardGroup.ToJsonObject());
        }
        return dict;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        try {
            setMaxHashAttempts(dict.getIntValue("maxHashAttempts"));
            List<String> keys = new ArrayList<String>();
            JSONArray keysArray = dict.getJSONArray("keys");
            for (int i = 0; i < keysArray.size(); i++) {
                keys.add(keysArray.getString(i));
            }
            setKeys(keys);
            if (dict.containsKey("shardGroup")) {
                ShardGroup shardGroup = new ShardGroup();
                shardGroup.FromJsonObject(dict.getJSONObject("shardGroup"));
                setShardGroup(shardGroup);
            }
        } catch (JSONException e) {
            throw new LogException("The HashConfig is invalid", e.getMessage(), e, "");
        }
    }

    public void FromJsonString(String logStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(logStoreString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("The HashConfig is invalid", e.getMessage(), e, "");
        }
    }

}
