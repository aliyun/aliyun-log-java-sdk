package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class SensitiveKey implements Serializable {
	private static final long serialVersionUID = 5303674958351023026L;
	private String key;
	private String type;
	private String regexBegin;
	private String regexContent;
	private boolean all;
	private String constStr;
	
	public SensitiveKey() {}
	public SensitiveKey(String key, String type, String regexBegin, String regexContent, boolean all, String constStr) {
		super();
		this.key = key;
		this.type = type;
		this.regexBegin = regexBegin;
		this.regexContent = regexContent;
		this.all = all;
		this.constStr = constStr;
	}

	public String getConstStr() {
		return constStr;
	}
	public void setConstStr(String constStr) {
		this.constStr = constStr;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRegexBegin() {
		return regexBegin;
	}
	public void setRegexBegin(String regexBegin) {
		this.regexBegin = regexBegin;
	}
	public String getRegexContent() {
		return regexContent;
	}
	public void setRegexContent(String regexContent) {
		this.regexContent = regexContent;
	}
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	
	public JSONObject ToJsonObject() {
		JSONObject sensitiveKeyJson = new JSONObject();
		sensitiveKeyJson.put(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_KEY, key);
		sensitiveKeyJson.put(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_TYPE, type);
		sensitiveKeyJson.put(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_REGEXBEGIN, regexBegin);
		sensitiveKeyJson.put(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_REGEXCONTENT, regexContent);
		sensitiveKeyJson.put(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_ALL, all);
		sensitiveKeyJson.put(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_CONST, constStr);
		
		return sensitiveKeyJson;
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		setKey(dict.getString(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_KEY));
		setType(dict.getString(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_TYPE));
		if (type.equals("const")) {
			setConstStr(dict.getString(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_CONST));
		}
		setRegexBegin(dict.getString(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_REGEXBEGIN));
		setRegexContent(dict.getString(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_REGEXBEGIN));
		setAll(dict.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_SENSITIVEKEYS_ALL));
	}
	
	public void FromJsonString(String sensitiveKeyString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(sensitiveKeyString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateChart", e.getMessage(), e, "");
		}
	}
}
