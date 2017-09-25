package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class OssShipperStorageColumn implements Serializable {
	
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
	
	public JSONObject ToJsonObject() {
		JSONObject configDict = new JSONObject();
		configDict.put("name", getName());
		configDict.put("type", getType());
		return configDict;
	}
	
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {			
			setName(dict.getString("name"));
			setType(dict.getString("type"));
		} catch (JSONException e) {
			throw new LogException("FailToGenerateColumn",  e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String columnString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(columnString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateColumn",  e.getMessage(), e, "");
		}
	}
}
