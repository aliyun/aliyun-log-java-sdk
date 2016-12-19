/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;

/**
 * QueriedLog used to present a log, it contain log source(e.g ip address) and log content. 
 * It is used to save the queried data from sls.
 * 
 * @author sls_dev
 * 
 */
public class QueriedLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2351757311270721859L;
	public String mSource;
	public LogItem mLogItem;
	/**
	 * Construct a logItem, the log time is set according to the sys time
	 *
	 * @param source source of the log
	 * @param logItem the log data item
	 */
	public QueriedLog(String source, LogItem logItem) {
		mSource = source;
		mLogItem = logItem;
	}
	
	/**
	 * Get queried log source
	 * @return  log source
	 */
	public String GetSource()
	{
		return mSource;
	}
	/**
	 * Return the log item content
	 * @return log item content
	 */
	public LogItem GetLogItem()
	{
		return mLogItem;
	}
}
