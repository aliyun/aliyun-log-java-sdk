package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class Domain implements Serializable, JsonSerializable, JsonDeserializable {

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

	@InternalApi
	public JSONObject toJsonObject() {
		JSONObject domainObject = new JSONObject();
		domainObject.put("domainName", getDomainName());
		return domainObject;
	}
	
	
	@InternalApi
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
