/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Consts.CompressType;
import com.aliyun.openservices.log.common.TagContent;

/**
 * The request used to send data to sls server
 * 
 * @author sls_dev
 * 
 */
public class PutLogsRequest extends Request {
	private static final long serialVersionUID = 7226856831224917838L;
	private String mLogStore;
	private String mTopic;
	private String mSource;
	private String mHashKey;
	private ArrayList<LogItem> mlogItems;
	private ArrayList<TagContent> mTags = null;
	private CompressType compressType = CompressType.LZ4;
	private String mContentType = Consts.CONST_PROTO_BUF;
	private byte[] mLogGroupBytes = null;
	/**
	 * @return the compressType
	 */
	public CompressType GetCompressType() {
		return compressType;
	}

	/**
	 * @param compressType the compressType to set
	 */
	public void SetCompressType(CompressType compressType) {
		this.compressType = compressType;
	}
	
	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String contentType) {
		this.mContentType = contentType;
	}

	/**
	 * Construct a put log request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name of the project
	 * @param topic
	 *            topic name of the log store
	 * @param source
	 *            source of the log
	 * @param logItems
	 *            log data
	 * @param hashKey
	 * 			  hashKey
	 */
	public PutLogsRequest(String project, String logStore, String topic,
			String source, List<LogItem> logItems,String hashKey) {
		super(project);
		mLogStore = logStore;
		mTopic = topic;
		mSource = source;
		mlogItems = new ArrayList<LogItem>(logItems);
		mHashKey = hashKey;
	}
	/**
	 * Construct a put log request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name of the project
	 * @param topic
	 *            topic name of the log store
	 * @param source
	 *            source of the log
	 * @param logItems
	 *            log data
	 */
	public PutLogsRequest(String project, String logStore, String topic,
			String source, List<LogItem> logItems) {
		super(project);
		mLogStore = logStore;
		mTopic = topic;
		mSource = source;
		mlogItems = new ArrayList<LogItem>(logItems);
		mHashKey = null;
	}


	/**
	 * Construct a put log request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name of the project
	 * @param topic
	 *            topic name of the log store
	 * @param logItems
	 *            log data
	 */
	public PutLogsRequest(String project, String logStore, String topic,
			List<LogItem> logItems) {
		super(project);
		mLogStore = logStore;
		mTopic = topic;
		mlogItems = new ArrayList<LogItem>(logItems);
	}

	/**
	 * Construct a put log request
	 *
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store name of the project
	 * @param topic
	 *            topic name of the log store
	 * @param source
	 *            source of the log
	 * @param logGroupBytes
	 *            Porotbuf serialized string of LogGroup
	 * @param hashKey
	 * 			  hashKey
	 */
	public PutLogsRequest(String project, String logStore, String topic,
						  String source, byte[] logGroupBytes, String hashKey) {
		super(project);
		mLogStore = logStore;
		mTopic = topic;
		mSource = source;
		mLogGroupBytes = logGroupBytes;
		mHashKey = hashKey;
	}

	/**
	 * Get log store
	 * 
	 * @return log store
	 */
	public String GetLogStore() {
		return mLogStore;
	}

	/**
	 * Set log store
	 * 
	 * @param logStore
	 *            log store name
	 */
	public void SetLogStore(String logStore) {
		mLogStore = logStore;
	}

	/**
	 * Get the topic
	 * 
	 * @return the topic
	 */
	public String GetTopic() {
		return mTopic;
	}

	/**
	 * Set topic value
	 * 
	 * @param topic
	 *            topic value
	 */
	public void SetTopic(String topic) {
		mTopic = topic;
	}

	/**
	 * Get log source
	 * 
	 * @return log source
	 */
	public String GetSource() {
		return mSource;
	}

	/**
	 * Set log source
	 * 
	 * @param source
	 *            log source
	 */
	public void SetSource(String source) {
		mSource = source;
	}

	/**
	 * Get all the log data
	 * 
	 * @return log data
	 */
	public ArrayList<LogItem> GetLogItems() {
		return mlogItems;
	}

	/**
	 * Get all the tag
	 *
	 * @return tag
	 */
	public ArrayList<TagContent> GetTags() {
		return mTags;
	}

	/**
	 * Get all the logGroupBytes
	 *
	 * @return logGroupBytes
	 */
	public byte[] GetLogGroupBytes() { return mLogGroupBytes; }

	/**
	 * Set the log data , shallow copy is used to set the log data
	 * 
	 * @param logItems
	 *            log data
	 */
	public void SetlogItems(List<LogItem> logItems) {
		mlogItems = new ArrayList<LogItem>(logItems);
	}

	public void SetTags(List<TagContent> tags) { mTags = new ArrayList<TagContent>(tags); }
	
	public void SetRouteKey(String hashKey)
	{
		SetParam(Consts.CONST_ROUTE_KEY,hashKey);
	}
	public String GetRouteKey()
	{
		return mHashKey;
	}
}
