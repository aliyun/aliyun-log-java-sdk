package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class DelimiterConfigInputDetail extends LocalFileConfigInputDetail implements Serializable {

	private static final long serialVersionUID = 4751026529573902188L;
	private String separator = "";
	private String quote = "";
	private ArrayList<String> key = new ArrayList<String>();
	private String timeKey = "";
	private boolean autoExtend = true;
	private boolean acceptNoEnoughKeys = false;

	public DelimiterConfigInputDetail() {
		this.logType = Consts.CONST_CONFIG_LOGTYPE_DELIMITER;
	}
	
	public DelimiterConfigInputDetail(String logPath, 
			String filePattern,
			String separator,
			String quote,
			ArrayList<String> key,
			String timeKey,
			String timeFormat,
			boolean autoExtend,
			boolean localStorage) {
		super();
		this.logType = Consts.CONST_CONFIG_LOGTYPE_DELIMITER;
		this.logPath = logPath;
		this.filePattern = filePattern;
		this.separator = separator;
		this.quote = quote;
		this.key = key;
		this.timeKey = timeKey;
		this.timeFormat = timeFormat;
		this.autoExtend = autoExtend;
		this.localStorage = localStorage;
	}
	
	public String GetSeparator() {
		return separator;
	}

	public void SetSeparator(String separator) {
		this.separator = separator;
	}

	public String GetQuote() {
		return quote;
	}

	public void SetQuote(String quote) {
		this.quote = quote;
	}

	public ArrayList<String> GetKey() {
		return key;
	}

	public void SetKey(ArrayList<String> key) {
		this.key = key;
	}

	public void SetKey(JSONArray key) throws LogException {
		try {
			this.key = new ArrayList<String>();
			for (int i = 0; i < key.size(); i++) {
				this.key.add(key.getString(i));
			}
		} catch (JSONException e) {
			throw new LogException("FailToSetKey", e.getMessage(), e, "");
		}
	}
	
	public String GetTimeKey() {
		return timeKey;
	}

	public void SetTimeKey(String timeKey) {
		this.timeKey = timeKey;
	}

	public boolean isAutoExtend() {
		return autoExtend;
	}

	public void SetAutoExtend(boolean autoExtend) {
		this.autoExtend = autoExtend;
	}
	
	public boolean isAcceptNoEnoughKeys() {
		return acceptNoEnoughKeys;
	}

	public void setAcceptNoEnoughKeys(boolean acceptNoEnoughKeys) {
		this.acceptNoEnoughKeys = acceptNoEnoughKeys;
	}

	@Override
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		LocalFileConfigToJsonObject(jsonObj);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_SEPARATOR, separator);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_QUOTE, quote);
		
		JSONArray keyArray = new JSONArray();
		for (String vKey : key) {
			keyArray.add(vKey);
		}
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_KEY, keyArray);
		
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_TIMEKEY, timeKey);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_AUTOEXTEND, autoExtend);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_ACCEPTNOENOUGHKEYS, acceptNoEnoughKeys);
		
		return jsonObj;
	}

	@Override
	public void FromJsonObject(JSONObject inputDetail) throws LogException {
		LocalFileConfigFromJsonObject(inputDetail);
		this.separator = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_SEPARATOR);
		this.quote = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_QUOTE);
		SetKey(inputDetail.getJSONArray(Consts.CONST_CONFIG_INPUTDETAIL_KEY));
		this.timeKey = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_TIMEKEY);
		this.autoExtend = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_AUTOEXTEND);
		if (inputDetail.has(Consts.CONST_CONFIG_INPUTDETAIL_ACCEPTNOENOUGHKEYS))
			this.acceptNoEnoughKeys = inputDetail.getBoolean(Consts.CONST_CONFIG_INPUTDETAIL_ACCEPTNOENOUGHKEYS);
		else
			this.acceptNoEnoughKeys = false;
	}

}
