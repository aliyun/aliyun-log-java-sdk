package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class OssShipperStorageColumn implements Serializable, JsonSerializable, JsonDeserializable {
	
	private static final long serialVersionUID = -4734086474258335426L;
	protected String name = "";
	protected String type = "'";
	
	public OssShipperStorageColumn() {
	}
	
	public OssShipperStorageColumn(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@InternalApi
	public JSONObject toJsonObject() {
		JSONObject configDict = new JSONObject();
		configDict.put("name", getName());
		configDict.put("type", getType());
		return configDict;
	}
	
	
	@InternalApi
	public void fromJsonObject(JSONObject dict) throws LogException {
		try {			
			setName(dict.getString("name"));
			setType(dict.getString("type"));
		} catch (JSONException e) {
			throw new LogException("FailToGenerateColumn",  e.getMessage(), e, "");
		}
	}
	
	public void fromJsonString(String columnString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(columnString);
			fromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateColumn",  e.getMessage(), e, "");
		}
	}
}
