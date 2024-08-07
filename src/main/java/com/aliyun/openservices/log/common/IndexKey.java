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
	private boolean chn;
	private String alias = "";
	private String type = "text";
	private String embedding ="";
	private String vector_index = "";
	
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
		this.chn = false;
	}
	
	public IndexKey(List<String> token, boolean caseSensitive, String type) {
		SetToken(token);
		this.caseSensitive = caseSensitive;
		this.type = type;
		this.docValue = true;
		this.chn = false;
	}

	public IndexKey(List<String> token, boolean caseSensitive, String type, String alias) {
		SetToken(token);
		this.caseSensitive = caseSensitive;
		this.type = type;
		this.docValue = true;
		this.alias = alias;
		this.chn = false;
	}
	public IndexKey(List<String> token, boolean caseSensitive, String type, String alias, String embedding, String vector_index) {
		SetToken(token);
		this.caseSensitive = caseSensitive;
		this.type = type;
		this.docValue = true;
		this.alias = alias;
		this.chn = false;
		this.embedding = embedding;
		this.vector_index = vector_index;
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
		this.alias = other.alias;
		this.chn = other.chn;
		this.embedding = other.embedding;
		this.vector_index = other.vector_index;
	}


	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean IsChn() {
		return chn;
	}
	
	/*
	 * @param chn to be set
	 */
	public void SetChn(boolean chn) {
		this.chn = chn;
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

	/**
	 * @return the embedding
	 */
	public String getEmbedding() {
		return embedding;
	}

	/**
	 * @return the vector_index
	 */
	public String getVectorIndex() {
		return vector_index;
	}

	/**
	 * @param embedding
	 */
	public void setEmbedding(String embedding) {
		this.embedding = embedding;
	}

	/**
	 * @param vector_index
	 */
	public void setVectorIndex(String vector_index) {
		this.vector_index = vector_index;
	}

	public JSONObject ToRequestJson() throws LogException {
		JSONObject allKeys = new JSONObject();
		JSONArray tokenDict = new JSONArray();
		tokenDict.addAll(token);
		
		allKeys.put("type", GetType());
		// only text type require token & caseSensitive
		if (GetType().equals("text")) {
			allKeys.put("token", tokenDict);
			allKeys.put("caseSensitive", GetCaseSensitive());
			allKeys.put("chn", IsChn());
		}
		
		allKeys.put("doc_value", IsDocValue());
		allKeys.put("alias", getAlias());

		if(getEmbedding().equals("") == false) {
			allKeys.put("embedding",getEmbedding());
		}
		if(getVectorIndex().equals("") == false) {
			allKeys.put("vector_index",getVectorIndex());
		}
		return allKeys;
	}
	
	public String ToRequestString() throws LogException {	
		return ToRequestJson().toString();
	}
	
	public JSONObject ToJsonObject() throws LogException {
		return ToRequestJson();
	}
	 
	public String ToJsonString() throws LogException {	
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			
			if (dict.containsKey("chn")) {
				SetChn(dict.getBooleanValue("chn"));
			} else {
				SetChn(false);
			}
			
			if (dict.containsKey("alias"))
				setAlias(dict.getString("alias"));
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
			if (dict.containsKey("embedding")) {
				setEmbedding(dict.getString("embedding"));
			}
			if (dict.containsKey("vector_index")) {
				setVectorIndex(dict.getString("vector_index"));
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
