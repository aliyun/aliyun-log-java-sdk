package com.aliyun.openservices.log.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.annotation.InternalApi;

/**
 * Index config for keys
 *
 * @author log-service-dev
 */
public class IndexKeys implements JsonSerializable, JsonDeserializable {
    private Map<String, IndexKey> keys = new HashMap<String, IndexKey>();

    public IndexKeys() {
    }

    public IndexKeys(IndexKeys other) {
        keys = new HashMap<String, IndexKey>();
        for (Map.Entry<String, IndexKey> entry : other.GetKeys().entrySet()) {
            IndexKey indexKey = entry.getValue();
            if (indexKey instanceof IndexJsonKey) {
                keys.put(entry.getKey(), new IndexJsonKey((IndexJsonKey) indexKey));
            } else {
                keys.put(entry.getKey(), new IndexKey(indexKey));
            }
        }
    }

    public Map<String, IndexKey> GetKeys() {
        return keys;
    }

    public void AddKey(String key, IndexKey keyContent) {
        keys.put(key, keyContent);
    }

    public boolean isEmpty() {
        return keys == null || keys.isEmpty();
    }

    @InternalApi
    public JSONObject toRequestJson() throws LogException {
        JSONObject keysDict = new JSONObject();
        for (Map.Entry<String, IndexKey> entry : keys.entrySet()) {
            keysDict.put(entry.getKey(), entry.getValue().toRequestJson());
        }
        return keysDict;
    }

    public String toRequestString() throws LogException {
        return toRequestJson().toString();
    }

    @InternalApi
    public JSONObject toJsonObject() throws LogException {
        JSONObject keysDict = toRequestJson();
        return keysDict;
    }


    @InternalApi
    public void fromJsonObject(JSONObject dict) throws LogException {
        try {
            keys = new HashMap<String, IndexKey>();
            for (String key : dict.keySet()) {
                JSONObject value = dict.getJSONObject(key);
                IndexKey indexKey;
                if ("json".equals(value.getString("type"))) {
                    indexKey = new IndexJsonKey();
                } else {
                    indexKey = new IndexKey();
                }
                indexKey.fromJsonObject(value);
                AddKey(key, indexKey);
            }
        } catch (JSONException e) {
            throw new LogException("FailToGenerateIndexKeys", e.getMessage(), e, "");
        }
    }

    public void fromJsonString(String indexKeysString) throws LogException {
        try {
            JSONObject dict = JSONObject.parseObject(indexKeysString);
            fromJsonObject(dict);
        } catch (JSONException e) {
            throw new LogException("FailToGenerateIndexKeys", e.getMessage(), e, "");
        }
    }
}
