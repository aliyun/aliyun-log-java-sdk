package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListConfigRequest extends Request {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4256319035181006650L;

	public ListConfigRequest(String project) {
		super(project);
		SetConfigName(Consts.DEFAULT_REQUEST_PARAM_CONFIGNAME);
		SetLogstoreName(Consts.DEFAULT_REQUEST_PARAM_LOGSTORENAME);
		SetOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		SetSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}
	
	public ListConfigRequest(String project, int offset, int size) {
		super(project);
		SetConfigName(Consts.DEFAULT_REQUEST_PARAM_CONFIGNAME);
		SetLogstoreName(Consts.DEFAULT_REQUEST_PARAM_LOGSTORENAME);
		SetOffset(offset);
		SetSize(size);
	}
	
	public ListConfigRequest(String project, String configName, int offset, int size) {
		super(project);
		SetConfigName(configName);
		SetLogstoreName(Consts.DEFAULT_REQUEST_PARAM_LOGSTORENAME);
		SetOffset(offset);
		SetSize(size);
	}
	
	public ListConfigRequest(String project, String configName, String logstoreName, int offset, int size) {
		super(project);
		SetConfigName(configName);
		SetLogstoreName(logstoreName);
		SetOffset(offset);
		SetSize(size);
	}

	public String GetConfigName() {
		return GetParam(Consts.CONST_CONFIGNAME);
	}

	public void SetConfigName(String configName) {
		SetParam(Consts.CONST_CONFIGNAME, configName);
	}

	public String GetLogstoreName() {
		return GetParam(Consts.CONST_LOGSTORE_NAME);
	}
	
	public void SetLogstoreName(String logstoreName) {
		SetParam(Consts.CONST_LOGSTORE_NAME, logstoreName);
	}
	
	public int GetOffset() {
		return Integer.parseInt(GetParam(Consts.CONST_OFFSET));
	}

	public void SetOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	public int GetSize() {
		return Integer.parseInt(GetParam(Consts.CONST_SIZE));
	}

	public void SetSize(int size) {
		SetParam(Consts.CONST_SIZE, String.valueOf(size));
	}
	
}
