/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.common.Logs.Log;
import com.aliyun.openservices.log.common.Logs.Log.Content;
import com.aliyun.openservices.log.common.Logs.LogGroup;
/**
 * LogGroup is the basic data structure for send, contains meta and logs
 * 
 * @author sls_dev
 * 
 */

public class LogGroupData implements Serializable {

	private static final long serialVersionUID = -7939302281903476332L;
	protected String mReserved = "";
	protected String mTopic = "";
	protected String mSource = "";
	protected String mMachineUUID = "";
	protected ArrayList<LogItem> mLogs;
	protected LogGroup mLogGroup;
	/**
	 * Construct a empty LogGroup
	 */
	public LogGroupData() {
	}
	
	public LogGroupData(LogGroup logGroup) {
		mLogGroup = logGroup;
	}
	
	public LogGroup GetLogGroup() {
		return mLogGroup;
	}

	public void SetLogGroup(LogGroup mLogGroup) {
		this.mLogGroup = mLogGroup;
	}
	@Deprecated
	public LogGroupData(LogGroupData logGroup) {
		mReserved = logGroup.GetReserved();
		mTopic = logGroup.GetTopic();
		mSource = logGroup.GetSource();
		mMachineUUID = logGroup.GetMachineUUID();
		mLogs = logGroup.mLogs;
		mLogGroup = logGroup.mLogGroup;
	}
	@Deprecated
	public LogGroupData(String reserved, String topic, String source, String mMachineUUID, ArrayList<LogItem> logs) {
		mReserved = reserved;
		mTopic = topic;
		mSource = source;
		SetAllLogs(logs);
	}
	protected void AutoDeserilize()
	{
		if(mLogs != null)
		{
			return;
		}
		if (mLogGroup.hasCategory()) {
			SetReserved(mLogGroup.getCategory());
		}
		if (mLogGroup.hasTopic()) {
			SetTopic(mLogGroup.getTopic());
		}
		if (mLogGroup.hasSource()) {
			SetSource(mLogGroup.getSource());
		}
		if (mLogGroup.hasMachineUUID())
		{
			SetMachineUUID(mLogGroup.getMachineUUID());
		}
		List<Log> logs = mLogGroup.getLogsList();
		ArrayList<LogItem> logItems = new ArrayList<LogItem>();
		for (Log log:logs) {
			ArrayList<LogContent> logContents = new ArrayList<LogContent>();
			for(Content content:log.getContentsList()) {
				logContents.add(new LogContent(content.getKey(), content.getValue()));
			}
			logItems.add(new LogItem(log.getTime(), logContents));
		}
		
		SetAllLogs(logItems);
	}
	/**
	 * @return the logs
	 */
	@Deprecated
	public ArrayList<LogItem> GetAllLogs() {
		AutoDeserilize();
		return mLogs;
	}

	/**
	 * @param index the index of log array
	 * @return the log
	 */
	@Deprecated
	public LogItem GetLogByIndex(int index) {
		AutoDeserilize();
		return mLogs.get(index);
	}
	
	/**
	 * @param logs the logs to set
	 */
	@Deprecated
	public void SetAllLogs(ArrayList<LogItem> logs) {
		mLogs = logs;
	}

	/**
	 * @return the reserved
	 */
	@Deprecated
	public String GetReserved() {
		return mReserved;
	}

	/**
	 * @param reserved the reserved to set
	 */
	@Deprecated
	public void SetReserved(String reserved) {
		mReserved = reserved;
	}

	/**
	 * @return the topic
	 */
	@Deprecated
	public String GetTopic() {
		return mLogGroup.getTopic();
	}

	/**
	 * @param topic the topic to set
	 */
	@Deprecated
	public void SetTopic(String topic) {
		mTopic = topic;
	}

	/**
	 * @return the source
	 */
	@Deprecated
	public String GetSource() {
		return mLogGroup.getSource();
	}

	/**
	 * @param source the source to set
	 */
	@Deprecated
	public void SetSource(String source) {
		mSource = source;
	}
	@Deprecated
	public String GetMachineUUID()
	{
		return mLogGroup.getMachineUUID();
	}
	@Deprecated
	public void SetMachineUUID(String machineUUID)
	{
		mMachineUUID = machineUUID;
	}
	
}
