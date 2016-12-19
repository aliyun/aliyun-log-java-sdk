/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;

/**
 * LogContent is a simple data structure used in @LogItem, it presents a
 * key/value pair in @logItem.
 * 
 * @author sls_dev
 * 
 */
public class LogContent implements Serializable {
	private static final long serialVersionUID = 6042186396863898096L;
	public String mKey;
	public String mValue;

	/**
	 * Construct a empty log content
	 */
	public LogContent() {
	}

	/**
	 * Construct a log content pair
	 * 
	 * @param key
	 *            log content key
	 * @param value
	 *            log content value
	 */
	public LogContent(String key, String value) {
		this.mKey = key;
		this.mValue = value;
	}

	/**
	 * Get log content key
	 * 
	 * @return log content key
	 */
	public String GetKey() {
		return mKey;
	}

	/**
	 * Get log content value
	 * 
	 * @return log content value
	 */
	public String GetValue() {
		return mValue;
	}
}
