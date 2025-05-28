package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

public class ShardingPolicy implements Serializable {
    private static final long serialVersionUID = 7401057377332043832L;
    private ShardGroup shardGroup = null;
    private ShardHash shardHash = null;

    public ShardingPolicy() {
    }

    public ShardingPolicy(ShardGroup shardGroup) {
        this(shardGroup, null);
    }

    public ShardingPolicy(ShardGroup shardGroup, ShardHash shardHash) {
        this.shardGroup = shardGroup;
        this.shardHash = shardHash;
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

    public JSONObject ToJsonObject() {
        JSONObject dict = new JSONObject();
        if (shardGroup != null) {
            dict.put("shardGroup", shardGroup.ToJsonObject());
        }
        if (shardHash != null) {
            dict.put("shardHash", shardHash.ToJsonObject());
        }
        return dict;
    }

    public String ToJsonString() {
        return ToJsonObject().toString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        try {
            if (dict.containsKey("shardGroup")) {
                ShardGroup shardGroup = new ShardGroup();
                shardGroup.FromJsonObject(dict.getJSONObject("shardGroup"));
                setShardGroup(shardGroup);
            }
            if (dict.containsKey("shardHash")) {
                ShardHash shardHash = new ShardHash();
                shardHash.FromJsonObject(dict.getJSONObject("shardHash"));
                setShardHash(shardHash);
            }
        } catch (JSONException e) {
            throw new LogException("The shardingPolicy is invalid", e.getMessage(), e, "");
        }
    }

    public void FromJsonString(String logStoreString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(logStoreString);
            FromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("The shardingPolicy is invalid", e.getMessage(), e, "");
        }
    }

}
