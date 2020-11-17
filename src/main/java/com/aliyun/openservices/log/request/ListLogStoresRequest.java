/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

/**
 * The request used to list log store from sls server
 * @author sls_dev
 *
 */
public class ListLogStoresRequest extends Request {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1380724751189149725L;
	

	
	public ListLogStoresRequest(String project, int offset, int size, String logstoreName)
	{
		super(project);
		SetOffset(offset);
		SetSize(size);
		SetLogstoreName(logstoreName);
	}

	public ListLogStoresRequest(String project, int offset, int size, String logstoreName, String telemetryType)
	{
		super(project);
		SetOffset(offset);
		SetSize(size);
		SetLogstoreName(logstoreName);
		SetTelemetryType(telemetryType);
	}
	

	public void SetOffset(int offset)
	{
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}
	

	public void SetSize(int size)
	{
		SetParam(Consts.CONST_SIZE,String.valueOf(size));
	}

	public void SetLogstoreName(String logstoreName)
	{
		SetParam(Consts.CONST_LOGSTORE_NAME,logstoreName);
	}

	public void SetTelemetryType(String telemetryType)
	{
		SetParam(Consts.CONST_TETEMETRY_TYPE,telemetryType);
	}
}
