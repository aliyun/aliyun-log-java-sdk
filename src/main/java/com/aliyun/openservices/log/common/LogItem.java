/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import net.sf.json.JSONObject;

/**
 * LogItem used to present a log, it contains log time, log source(ip/hostname,
 * e.g), and multiple key/value pairs to present the log content
 * 
 * @author sls_dev
 * 
 */
public class LogItem implements Serializable {
	private static final long serialVersionUID = -3488075856612935955L;
	public int mLogTime;
	public ArrayList<LogContent> mContents = new ArrayList<LogContent>();

	/**
	 * Construct a logItem, the log time is set according to the sys time
	 */
	public LogItem() {
		this.mLogTime = (int) (new Date().getTime() / 1000);
	}

	/**
	 * Construct a logItem with a certain time stamp
	 * 
	 * @param logTime
	 *            log time stamp
	 */
	public LogItem(int logTime) {
		this.mLogTime = logTime;
	}
	
	/**
	 * Construct a logItem with a certain time stamp and log contents
	 * 
	 * @param logTime
	 *            log time stamp
	 * @param contents
	 *            log contents
	 */
	public LogItem(int logTime, ArrayList<LogContent> contents) {
		this.mLogTime = logTime;
		SetLogContents(contents);
	}

	/**
	 * Set logTime
	 * 
	 * @param logTime
	 *            log time
	 */
	public void SetTime(int logTime) {
		this.mLogTime = logTime;
	}

	/**
	 * Get log time
	 * 
	 * @return log time
	 */
	public int GetTime() {
		return mLogTime;
	}

	/**
	 * Add a log content key/value pair to the log
	 * 
	 * @param key
	 *            log content key
	 * @param value
	 *            log content value
	 */
	public void PushBack(String key, String value) {
		PushBack(new LogContent(key, value));
	}

	/**
	 * Add a log content to the log
	 * 
	 * @param content
	 *            log content
	 */
	public void PushBack(LogContent content) {
		mContents.add(content);
	}

	/**
	 * set log contents
	 * 
	 * @param contents
	 *            log contents
	 */
	public void SetLogContents(ArrayList<LogContent> contents) {
		mContents =  new ArrayList<LogContent>(contents);
	}

	
	/**
	 * Get log contents
	 * 
	 * @return log contents
	 */
	public ArrayList<LogContent> GetLogContents() {
		return mContents;
	}
	
	public String ToJsonString() {
		JSONObject obj = new JSONObject();
		
		obj.put("logtime", mLogTime);
		for(LogContent content : mContents) {
			obj.put(content.GetKey(), content.GetValue());
		}
		
		return obj.toString();
	}

}
