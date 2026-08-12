package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.annotation.InternalApi;


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
	@InternalApi
	public JSONObject toJsonObject() {
		JSONObject jsonObj = new JSONObject();
		localFileConfigToJsonObject(jsonObj);
		jsonObj.put(Consts.CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX, logBeginRegex);
		return jsonObj;
	}

	@Override
	@InternalApi
	public void fromJsonObject(JSONObject inputDetail) throws LogException {
		localFileConfigFromJsonObject(inputDetail);
		this.logBeginRegex = inputDetail.getString(Consts.CONST_CONFIG_INPUTDETAIL_LOGBEGINREGEX);
	}

}
