package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

public class ShardingPolicy implements Serializable, JsonSerializable, JsonDeserializable {

    private static final long serialVersionUID = -1477618902401594872L;

    private ShardGroup shardGroup;
    private ShardHash shardHash;
    private Long queryActiveTime;

    public ShardingPolicy() {
    }

    public ShardingPolicy(ShardGroup shardGroup, ShardHash shardHash) {
        this.shardGroup = shardGroup;
        this.shardHash = shardHash;
    }

    public ShardingPolicy(ShardGroup shardGroup, ShardHash shardHash, Long queryActiveTime) {
        this.shardGroup = shardGroup;
        this.shardHash = shardHash;
        this.queryActiveTime = queryActiveTime;
    }

    public ShardingPolicy(ShardingPolicy shardingPolicy) {
        if (shardingPolicy != null) {
            this.shardGroup = shardingPolicy.shardGroup == null ? null : new ShardGroup(shardingPolicy.shardGroup);
            this.shardHash = shardingPolicy.shardHash == null ? null : new ShardHash(shardingPolicy.shardHash);
            this.queryActiveTime = shardingPolicy.queryActiveTime;
        }
    }

    public ShardGroup getShardGroup() {
        return shardGroup;
    }

    public void setShardGroup(ShardGroup shardGroup) {
        this.shardGroup = shardGroup;
    }

    public ShardHash getShardHash() {
        return shardHash;
    }

    public void setShardHash(ShardHash shardHash) {
        this.shardHash = shardHash;
    }

    public Long getQueryActiveTime() {
        return queryActiveTime;
    }

    public void setQueryActiveTime(Long queryActiveTime) {
        this.queryActiveTime = queryActiveTime;
    }

    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject dict = new JSONObject();
        if (shardGroup != null) {
            dict.put("shardGroup", shardGroup.toJsonObject());
        }
        if (shardHash != null) {
            dict.put("shardHash", shardHash.toJsonObject());
        }
        if (queryActiveTime != null) {
            dict.put("queryActiveTime", queryActiveTime);
        }
        return dict;
    }


    @InternalApi
    public void fromJsonObject(JSONObject dict) throws LogException {
        try {
            if (dict.containsKey("shardGroup")) {
                JSONObject shardGroupDict = dict.getJSONObject("shardGroup");
                this.shardGroup = null;
                if (shardGroupDict != null) {
                    ShardGroup group = new ShardGroup();
                    group.fromJsonObject(shardGroupDict);
                    this.shardGroup = group;
                }
            }
            if (dict.containsKey("shardHash")) {
                JSONObject shardHashDict = dict.getJSONObject("shardHash");
                this.shardHash = null;
                if (shardHashDict != null) {
                    ShardHash hash = new ShardHash();
                    hash.fromJsonObject(shardHashDict);
                    this.shardHash = hash;
                }
            }
            if (dict.containsKey("queryActiveTime")) {
                this.queryActiveTime = dict.getLong("queryActiveTime");
            }
        } catch (JSONException e) {
            throw new LogException("FailToGenerateShardingPolicy", e.getMessage(), e, "");
        }
    }

    public void fromJsonString(String shardingPolicyString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(shardingPolicyString);
            fromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateShardingPolicy", e.getMessage(), e, "");
        }
    }

    public static class ShardGroup implements Serializable {

        private static final long serialVersionUID = -7478244868846044687L;

        private List<String> keys = new ArrayList<String>();
        private Integer groupCount;

        public ShardGroup() {
        }

        public ShardGroup(List<String> keys, Integer groupCount) {
            setKeys(keys);
            this.groupCount = groupCount;
        }

        public ShardGroup(ShardGroup shardGroup) {
            if (shardGroup != null) {
                setKeys(shardGroup.keys);
                this.groupCount = shardGroup.groupCount;
            }
        }

        public List<String> getKeys() {
            return keys;
        }

        public void setKeys(List<String> keys) {
            this.keys = keys == null ? new ArrayList<String>() : new ArrayList<String>(keys);
        }

        public Integer getGroupCount() {
            return groupCount;
        }

        public void setGroupCount(Integer groupCount) {
            this.groupCount = groupCount;
        }

        @InternalApi
        public JSONObject toJsonObject() {
            JSONObject dict = new JSONObject();
            dict.put("keys", toJsonArray(keys));
            if (groupCount != null) {
                dict.put("groupCount", groupCount);
            }
            return dict;
        }

        @InternalApi
        public void fromJsonObject(JSONObject dict) {
            if (dict.containsKey("keys")) {
                setKeys(toStringList(dict.getJSONArray("keys")));
            }
            if (dict.containsKey("groupCount")) {
                this.groupCount = dict.getInteger("groupCount");
            }
        }
    }

    public static class ShardHash implements Serializable {

        private static final long serialVersionUID = 4206232128505123809L;

        private List<String> keys = new ArrayList<String>();
        private Integer maxHashCount = 2;

        public ShardHash() {
        }

        public ShardHash(List<String> keys, Integer maxHashCount) {
            setKeys(keys);
            this.maxHashCount = maxHashCount;
        }

        public ShardHash(ShardHash shardHash) {
            if (shardHash != null) {
                setKeys(shardHash.keys);
                this.maxHashCount = shardHash.maxHashCount;
            }
        }

        public List<String> getKeys() {
            return keys;
        }

        public void setKeys(List<String> keys) {
            this.keys = keys == null ? new ArrayList<String>() : new ArrayList<String>(keys);
        }

        public Integer getMaxHashCount() {
            return maxHashCount;
        }

        public void setMaxHashCount(Integer maxHashCount) {
            this.maxHashCount = maxHashCount;
        }

        @InternalApi
        public JSONObject toJsonObject() {
            JSONObject dict = new JSONObject();
            dict.put("keys", toJsonArray(keys));
            if (maxHashCount != null) {
                dict.put("maxHashCount", maxHashCount);
            }
            return dict;
        }

        @InternalApi
        public void fromJsonObject(JSONObject dict) {
            if (dict.containsKey("keys")) {
                setKeys(toStringList(dict.getJSONArray("keys")));
            }
            if (dict.containsKey("maxHashCount")) {
                this.maxHashCount = dict.getInteger("maxHashCount");
            }
        }
    }

    private static List<String> toStringList(JSONArray array) {
        List<String> result = new ArrayList<String>();
        if (array == null) {
            return result;
        }
        for (int i = 0; i < array.size(); i++) {
            result.add(array.getString(i));
        }
        return result;
    }

    private static JSONArray toJsonArray(List<String> values) {
        JSONArray array = new JSONArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }
}
