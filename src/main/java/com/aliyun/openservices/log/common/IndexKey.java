package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

/**
 * Index config of a key
 * @author log-service-dev
 *
 */
public class IndexKey implements Serializable {

	private static final long serialVersionUID = -6607480102839653253L;
	private List<String> token = new ArrayList<String>();
	private boolean caseSensitive;
	private boolean docValue;
	private String type;
	
	public IndexKey() {
	}

	/**
	 * create Index config for a key
	 * @param token the token used to split log data
	 * @param caseSensitive  true is case sensitive
	 */
	public IndexKey(List<String> token, boolean caseSensitive) {
		SetToken(token);
		this.caseSensitive = caseSensitive;
		this.type = "text";
		this.docValue = true;
	}
	
	public IndexKey(List<String> token, boolean caseSensitive, String type) {
		SetToken(token);
		this.caseSensitive = caseSensitive;
		this.type = type;
		this.docValue = true;
	}
	
	/**
	 * create index config from another index key
	 * @param other another index key
	 */
	public IndexKey(IndexKey other) {
		SetToken(other.GetToken());
		this.caseSensitive = other.GetCaseSensitive();
		this.type = other.GetType();
		this.docValue = other.IsDocValue();
	}
	
	/**
	 * @return type
	 */
	public boolean IsDocValue() {
		return docValue;
	}
	
	/**
	 * @param docValue to be set
	 */
	public void SetDocValue(boolean docValue) {
		this.docValue = docValue;
	}
	/**
	 * @return type
	 */
	public String GetType() {
		return type;
	}
	
	/**
	 * @param type to be set
	 */
	public void SetType(String type) {
		this.type = type;
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
	
	public JSONObject ToRequestJson() throws LogException {
		JSONObject allKeys = new JSONObject();
		JSONArray tokenDict = new JSONArray();
		for (String v:token) {
			tokenDict.add(v);
		}
		
		allKeys.put("type", GetType());
		// only text type require token & caseSensitive
		if (GetType().equals("text")) {
			allKeys.put("token", tokenDict);
			allKeys.put("caseSensitive", GetCaseSensitive());
		}
		
		allKeys.put("doc_value", IsDocValue());
		
		return allKeys;
	}
	
	public String ToRequestString() throws LogException {	
		return ToRequestJson().toString();
	}
	
	public JSONObject ToJsonObject() throws LogException {
		JSONObject allKeys = ToRequestJson();
		return allKeys;
	}
	 
	public String ToJsonString() throws LogException {	
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			if (!dict.containsKey("doc_value"))
				SetDocValue(false);
			else
				SetDocValue(dict.getBooleanValue("doc_value"));
			boolean caseSensitive = false;
			if (dict.containsKey("caseSensitive"))
				caseSensitive = dict.getBooleanValue("caseSensitive");
			if (dict.containsKey("type"))
				SetType(dict.getString("type"));
			else
				SetType("text");
			SetCaseSensitive(caseSensitive);
			JSONArray tokenDict = new JSONArray();
			if (dict.containsKey("token"))
				tokenDict = dict.getJSONArray("token");
			token = new ArrayList<String>();
			for (int i = 0;i < tokenDict.size();i++) {
				token.add(tokenDict.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndexKey", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String indexKeyString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(indexKeyString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateIndexKey", e.getMessage(), e, "");
		}
	}
	
	
}
