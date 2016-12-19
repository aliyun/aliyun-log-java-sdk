package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONObject;

public class ApsaraLogConfigInputDetail extends LocalFileConfigInputDetail implements Serializable {

	private static final long serialVersionUID = 2473941167679780029L;
	private String logBeginRegex = "";

	public ApsaraLogConfigInputDetail() {
		this.logType = Consts.CONST_CONFIG_LOGTYPE_APSARA;
	}
	
	public String GetLogBeginRegex() {
		return logBeginRegex;
	}
	
	public void SetLogBeginRegex(String logBeginRegex) {
		this.logBeginRegex = logBeginRegex;
	}
	
	public ApsaraLogConfigInputDetail(String logPath, 
			String filePattern,
			String logBeginRegex,
			boolean localStorage) {
		super();
		this.logType = Consts.CONST_CONFIG_LOGTYPE_APSARA;
		this.logPath = logPath;
		this.filePattern = filePattern;
		this.logBeginRegex = logBeginRegex;
		this.localStorage = localStorage;
	}
	
	@Override
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		LocalFileConfigToJsonObject(jsonObj);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX, logBeginRegex);
		return jsonObj;
	}

	@Override
	public void FromJsonObject(JSONObject inputDetail) throws LogException {
		LocalFileConfigFromJsonObject(inputDetail);
		this.logBeginRegex = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX);
	}

}
