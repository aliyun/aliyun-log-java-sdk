package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.io.Serializable;
import java.util.List;

/**
 * Index config of a josn key
 * @author log-service-dev
 *
 */
public class IndexJsonKey extends IndexKey implements Serializable {

	private boolean indexAll = true;
	private int maxDepth = -1;

	private IndexKeys jsonKeys = new IndexKeys();

	public IndexJsonKey() {
		super();
		this.SetType("json");
	}

	public IndexJsonKey(List<String> token, boolean caseSensitive) {
		super(token, caseSensitive);
		this.SetType("json");
	}

	public IndexJsonKey(List<String> token, boolean caseSensitive, String type) {
		super(token, caseSensitive, type);
		this.SetType("json");
	}

	public IndexJsonKey(List<String> token, boolean caseSensitive, String type, String alias) {
		super(token, caseSensitive, type, alias);
		this.SetType("json");
	}

	public IndexJsonKey(IndexJsonKey other) {
		super(other);
		this.SetType("json");
		setIndexAll(other.isIndexAll());
		setMaxDepth(other.getMaxDepth());
		setJsonKeys(new IndexKeys(other.getJsonKeys()));
	}

	public boolean isIndexAll() {
		return indexAll;
	}

	public void setIndexAll(boolean indexAll) {
		this.indexAll = indexAll;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public IndexKeys getJsonKeys() {
		return jsonKeys;
	}

	public void setJsonKeys(IndexKeys jsonKeys) {
		this.jsonKeys = jsonKeys;
	}

	@Override
	public void FromJsonObject(JSONObject dict) throws LogException {
		super.FromJsonObject(dict);
		if (dict.containsKey("index_all")) {
			setIndexAll(dict.getBooleanValue("index_all"));
		}
		if (dict.containsKey("max_depth")) {
			setMaxDepth(dict.getIntValue("max_depth"));
		}
		if (dict.containsKey("json_keys")) {
			JSONObject jsonObject = dict.getJSONObject("json_keys");
			jsonKeys.FromJsonObject(jsonObject);
		}
	}

	@Override
	public JSONObject ToRequestJson() throws LogException {
		JSONObject allKeys =  super.ToRequestJson();
		JSONArray tokenDict = new JSONArray();
		for (String v:GetToken()) {
			tokenDict.add(v);
		}
		if ("json".equals(GetType())) {
			allKeys.put("token", tokenDict);
			allKeys.put("caseSensitive", GetCaseSensitive());
			allKeys.put("chn", IsChn());
		}
		allKeys.put("index_all", isIndexAll());
		allKeys.put("max_depth", getMaxDepth());
		allKeys.put("json_keys",getJsonKeys().ToRequestJson());
		return allKeys;
	}
}
