package com.aliyun.openservices.log.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

/**
 * Index config for keys
 * @author log-service-dev
 *
 */
public class IndexKeys {
	private Map<String, IndexKey> keys = new HashMap<String, IndexKey>();

	public IndexKeys() {
	}

	public IndexKeys(IndexKeys other) {
		keys = new HashMap<String, IndexKey>();
		for (Map.Entry<String, IndexKey> entry : other.GetKeys().entrySet()) {
			IndexKey indexKey = entry.getValue();
			if(indexKey instanceof IndexJsonKey){
				keys.put(entry.getKey(), new IndexJsonKey((IndexJsonKey) indexKey));
			}else {
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
	
	public JSONObject ToRequestJson() throws LogException {
		JSONObject keysDict = new JSONObject();
		for (Map.Entry<String, IndexKey> entry : keys.entrySet()) {
			keysDict.put(entry.getKey(), entry.getValue().ToRequestJson());
		}
		return keysDict;
	}
	
	public String ToRequestString() throws LogException {	
		return ToRequestJson().toString();
	}
	
	public JSONObject ToJsonObject() throws LogException {
		JSONObject keysDict = ToRequestJson();
		return keysDict;
	}
	 
	public String ToJsonString() throws LogException {	
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			keys = new HashMap<String, IndexKey>();
			Iterator<String> it = dict.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				JSONObject value = dict.getJSONObject(key);
				IndexKey indexKey;
				if("json".equals(value.getString("type"))){
					indexKey = new IndexJsonKey();
				}else {
					indexKey = new IndexKey();
				}
				indexKey.FromJsonObject(value);
				AddKey(key, indexKey);
			}
		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndexKeys", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String indexKeysString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(indexKeysString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndexKeys", e.getMessage(), e, "");
		}
	}
}
