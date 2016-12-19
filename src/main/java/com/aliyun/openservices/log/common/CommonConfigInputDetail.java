package com.aliyun.openservices.log.common;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public abstract class CommonConfigInputDetail {
	protected boolean localStorage = true;
	protected ArrayList<String> filterRegex = new ArrayList<String>();
	protected ArrayList<String> filterKey = new ArrayList<String>();
	protected ArrayList<String> shardHashKey = new ArrayList<String>();
	protected boolean enableTag = false;
	
	public boolean GetEnableTag() {
		return enableTag;
	}

	public void SetEnableTag(boolean enableTag) {
		this.enableTag = enableTag;
	}
	
	public boolean GetLocalStorage() 
	{
		return this.localStorage;
	}
	
	public void SetLocalStorage(boolean localStorage)
	{
		this.localStorage = localStorage;
	}
	
	public void SetFilterKeyRegex(List<String> filterKey, List<String> filterRegex) {
		this.filterKey = new ArrayList<String>(filterKey);
		this.filterRegex = new ArrayList<String>(filterRegex);
	}
	
	public ArrayList<String> GetShardHashKey() {
		return shardHashKey;
	}
	
	public void SetShardHashKeyList(List<String> shardHashKey) {
		this.shardHashKey = new ArrayList<String>(shardHashKey);
	}
	
	public void SetShardHashKey(JSONArray shardHashKey) throws LogException {
		try {
			this.shardHashKey = new ArrayList<String>();
			for (int i = 0; i < shardHashKey.size(); i++)
				this.shardHashKey.add(shardHashKey.getString(i));
		} catch (JSONException e) {
			throw new LogException("FailToSetShardHashKey", e.getMessage(), e, "");
		}
	}
	
	public ArrayList<String> GetFilterRegex() {
		return filterRegex;
	}
	
	public void SetFilterRegex(JSONArray filterRegex) throws LogException {
		try {
			this.filterRegex = new ArrayList<String>();
			for (int i = 0; i < filterRegex.size(); i++) {
				this.filterRegex.add(filterRegex.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("FailToSetFilterRegex", e.getMessage(), e, "");
		}
	}
	
	public ArrayList<String> GetFilterKey() {
		return filterKey;
	}
	
	public void SetFilterKey(JSONArray filterKey) throws LogException {
		try {
			this.filterKey = new ArrayList<String>();
			for (int i = 0; i < filterKey.size(); i++) {
				this.filterKey.add(filterKey.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("FailToSetFilterKey", e.getMessage(), e, "");
		}
	}
	
	public abstract JSONObject ToJsonObject();
	public abstract void FromJsonObject(JSONObject inputDetail) throws LogException;
	
	protected void CommonConfigToJsonObject(JSONObject jsonObj) {
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOCALSTORAGE, localStorage);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ENABLETAG, enableTag);
		JSONArray filterRegexArray = new JSONArray();
		for (String fr : filterRegex) {
			filterRegexArray.add(fr);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_FILTERREGEX, filterRegexArray);

		JSONArray filterKeyArray = new JSONArray();
		for (String fk : filterKey) {
			filterKeyArray.add(fk);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_FILTERKEY, filterKeyArray);
		
		JSONArray shardHashKeyArray = new JSONArray();
		for (String shk : shardHashKey) {
			shardHashKeyArray.add(shk);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_SHARDHASHKEY, shardHashKeyArray);
	}
	
	protected void CommonConfigFromJsonObject(JSONObject inputDetail) throws LogException {
		try {
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_LOCALSTORAGE))
				this.localStorage = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_LOCALSTORAGE);
			else
				this.localStorage = true;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_ENABLETAG))
				this.enableTag = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ENABLETAG);
			else
				this.enableTag = false;
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_FILTERREGEX))
				SetFilterRegex(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_FILTERREGEX));
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_FILTERKEY))
				SetFilterKey(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_FILTERKEY));
			if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_SHARDHASHKEY))
				SetShardHashKey(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_SHARDHASHKEY));
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(), e, "");
		}
	}
	
	public static CommonConfigInputDetail FromJsonStringS(final String inputType, final String jsonString) throws LogException 
	{
		try {
			JSONObject inputDetail = JSONObject.fromObject(jsonString);
			return FromJsonObjectS(inputType, inputDetail);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(),
					e, "");
		}
	}
	public static CommonConfigInputDetail FromJsonObjectS(final String inputType, JSONObject inputDetail) throws LogException 
	{
		try {
			if (inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_SYSLOG) 
					|| inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_STREAMLOG)) {
				StreamLogConfigInputDetail res = new StreamLogConfigInputDetail();
				res.FromJsonObject(inputDetail);
				return res;
			} else if (inputType.equals(Consts.CONST_CONFIG_INPUTTYPE_FILE)) {
				if (inputDetail.containsKey(Consts.CONST_CONFIG_LOGTYPE)) {
					if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE).compareTo(Consts.CONST_CONFIG_LOGTYPE_JSON) == 0) {
						JsonConfigInputDetail res = new JsonConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE).compareTo(Consts.CONST_CONFIG_LOGTYPE_DELIMITER) == 0) {
						DelimiterConfigInputDetail res = new DelimiterConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE).compareTo(Consts.CONST_CONFIG_LOGTYPE_APSARA) == 0) {
						ApsaraLogConfigInputDetail res = new ApsaraLogConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else if (inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE).compareTo(Consts.CONST_CONFIG_LOGTYPE_COMMON) == 0) {
						ConfigInputDetail res = new ConfigInputDetail();
						res.FromJsonObject(inputDetail);
						return res;
					} else {
						throw new LogException("FailToGenerateInputDetail", "invlaid logType", inputDetail.getString(Consts.CONST_CONFIG_LOGTYPE));
					}
				} else {
					throw new LogException("FailToGenerateInputDetail", "logType field does not exist in input detail", "");
				}
			}
			else
			{
				throw new LogException("FailToGenerateInputDetail", "invalid inputType", inputType);
			}
		} catch (JSONException e) {
			throw new LogException("FailToGenerateInputDetail", e.getMessage(),
					e, "");
		}
	}
}
