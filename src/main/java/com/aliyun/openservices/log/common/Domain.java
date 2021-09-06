package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class Domain implements Serializable {

	private static final long serialVersionUID = 7764718041155026943L;
	private String domainName = "";
	
	public Domain() {}
	
	public Domain(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public JSONObject toJsonObject() {
		JSONObject domainObject = new JSONObject();
		domainObject.put("domainName", getDomainName());
		return domainObject;
	}
	
	public String toJsonString() {
		return toJsonObject().toString();
	}
	
	public void fromJsonObject(JSONObject dict) throws LogException {
		try {
			setDomainName(dict.getString("domainName"));
		} catch (JSONException ex) {
			throw new LogException("FailToGenerateDomain",  ex.getMessage(), ex, "");
		}
	}
	
	public void fromJsonString(String domainString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(domainString);
			fromJsonObject(dict);
		} catch (JSONException ex) {
			throw new LogException("FailToGenerateDomain", ex.getMessage(), ex, "");
		}
	}
}
