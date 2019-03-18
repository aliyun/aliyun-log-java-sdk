package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

public class IndexLine {
	private List<String> token = new ArrayList<String>();
	private boolean caseSensitive;
	private boolean chn;
	private List<String> includeKeys = new ArrayList<String>();
	private List<String> excludeKeys = new ArrayList<String>();
	
	public IndexLine() {
		
	}
	
	public IndexLine(List<String> token, boolean caseSensitive) {
		SetToken(token);
		this.caseSensitive = caseSensitive;
		this.chn = false;
	}
	
	public IndexLine(IndexLine other) {
		SetToken(other.GetToken());
		this.caseSensitive = other.GetCaseSensitive();
		this.chn = other.GetChn();
		SetIncludeKeys(other.GetIncludeKeys());
		SetExcludeKeys(other.GetExcludeKeys());
	}
	
	/**
	 * @return the token
	 */
	public List<String> GetToken() {
		return token;
	}
	/**
	 * @return the caseSensitive
	 */
	public boolean GetCaseSensitive() {
		return caseSensitive;
	}
	/**
	 * @return chn
	 */
	public boolean GetChn() {
		return chn;
	}
	/**
	 * @param token the token to set
	 */
	public void SetToken(List<String> token) {
		this.token = new ArrayList<String>(token);
	}
	/**
	 * @param caseSensitive the caseSensitive to set
	 */
	public void SetCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	/**
	 * @param chn to set
	 */
	public void SetChn(boolean chn) {
		this.chn = chn;
	}
		
	/**
	 * @return the includeKeys
	 */
	public List<String> GetIncludeKeys() {
		return includeKeys;
	}

	/**
	 * @return the excludeKeys
	 */
	public List<String> GetExcludeKeys() {
		return excludeKeys;
	}

	/**
	 * @param includeKeys the includeKeys to set
	 */
	public void SetIncludeKeys(List<String> includeKeys) {
		this.includeKeys = new ArrayList<String>(includeKeys);
	}

	/**
	 * @param excludeKeys the excludeKeys to set
	 */
	public void SetExcludeKeys(List<String> excludeKeys) {
		this.excludeKeys = new ArrayList<String>(excludeKeys);
	}

	public JSONObject ToRequestJson() {
		JSONObject line = new JSONObject();
		JSONArray tokenDict = new JSONArray();
		for (String v:token) {
			tokenDict.add(v);
		}
		line.put("token", tokenDict);
		
		if (includeKeys.size() > 0) {
			JSONArray includeKeysDict = new JSONArray();
			for (String v:includeKeys) {
				includeKeysDict.add(v);
			}
			line.put("include_keys", includeKeysDict);
		}
		
		if (excludeKeys.size() > 0) {
			JSONArray excludeKeysDict = new JSONArray();
			for (String v:excludeKeys) {
				excludeKeysDict.add(v);
			}
			line.put("exclude_keys", excludeKeysDict);
		}
		
		line.put("caseSensitive", GetCaseSensitive());
		line.put("chn", GetChn());
		
		return line;
	}
	
	public String ToRequestString() {	
		return ToRequestJson().toString();
	}
	
	public JSONObject ToJsonObject() {
		JSONObject line = ToRequestJson();
		return line;
	}
	 
	public String ToJsonString() {	
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			if (dict.containsKey("caseSensitive")) {
				SetCaseSensitive(dict.getBoolean("caseSensitive"));
			} else {
				SetCaseSensitive(false);
			}
			if (dict.containsKey("chn")) {
				SetChn(dict.getBooleanValue("chn"));
			} else {
				SetChn(false);
			}
			
			JSONArray tokenDict = dict.getJSONArray("token");
			token = new ArrayList<String>();
			for (int i = 0;i < tokenDict.size();i++) {
				token.add(tokenDict.getString(i));
			}
			
			if (dict.containsKey("include_keys")) {
				JSONArray includeKeysDict = dict.getJSONArray("include_keys");
				includeKeys = new ArrayList<String>();
				for (int i = 0;i < includeKeysDict.size();i++) {
					includeKeys.add(includeKeysDict.getString(i));
				}
			}
			
			if (dict.containsKey("exclude_keys")) {
				JSONArray excludeKeysDict = dict.getJSONArray("exclude_keys");
				excludeKeys = new ArrayList<String>();
				for (int i = 0;i < excludeKeysDict.size();i++) {
					excludeKeys.add(excludeKeysDict.getString(i));
				}
			}
			
		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndexLine", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String indexLineString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(indexLineString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndexLine", e.getMessage(), e, "");
		}
	}
	
}
