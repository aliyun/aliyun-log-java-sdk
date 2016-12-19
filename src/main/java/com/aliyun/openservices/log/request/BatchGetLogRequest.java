/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

/**
 * The request used to get log data from loghub server
 * @author sls_dev
 *
 */
public class BatchGetLogRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4467245335122126584L;
	protected String mLogStore;
	protected int mShardId;
	
	/**
	 * Construct a get cursor request
	 * @param project
	 *            project name
	 * @param logStore
	 *            log stream name
	 * @param shardId
	 * 			  shard id
	 * @param cursor
	 * 			  current cursor
	 * @param count
	 * 			  loggroup num
	 */
	public BatchGetLogRequest(String project, String logStore, int shardId, int count, String cursor) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetCursor(cursor);
		SetCount(count);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_LOG);
	}

	
	/**
	 * Construct a get cursor request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log stream name
	 * @param shardId
	 *            shard id
	 * @param cursor
	 *            current cursor
	 * @param count
	 *            loggroup num
	 * @param end_cursor
	 *            the end cursor
	 */
	public BatchGetLogRequest(String project, String logStore, int shardId, int count, String cursor, String end_cursor) {
		super(project);
		this.mLogStore = logStore;
		this.mShardId = shardId;
		SetCursor(cursor);
		SetEndCursor(end_cursor);
		SetCount(count);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_LOG);
	}
	
	/**
	 * @return the logStore
	 */
	public String GetLogStore() {
		return mLogStore;
	}

	/**
	 * @param logStore the logStore to set
	 */
	public void SetLogStore(String logStore) {
		this.mLogStore = logStore;
	}
	
	/**
	 * @return the shardId
	 */
	public int GetShardId() {
		return mShardId;
	}

	/**
	 * @param shardId the shardId to set
	 */
	public void SetShardId(int shardId) {
		this.mShardId = shardId;
	}
	
	/**
	 * @return the cursor
	 */
	public String GetCursor() {
		return GetParam(Consts.CONST_CURSOR);
	}

	/**
	 * @param cursor the cursor to set
	 */
	public void SetCursor(String cursor) {
		SetParam(Consts.CONST_CURSOR, cursor);
	}
	
	/**
	 * get the end cursor
	 * @return end cursor
	 */
	public String GetEndCursor()
	{
		return GetParam(Consts.CONST_END_CURSOR);
	}
	/**
	 * set end cursor
	 * @param end_cursor the end cursor
	 */
	public void SetEndCursor(String end_cursor) {
		if (end_cursor != null) {
			SetParam(Consts.CONST_END_CURSOR, end_cursor);
		}
	}
	/**
	 * @return the count
	 */
	public int GetCount() {
		return Integer.parseInt(GetParam(Consts.CONST_COUNT));
	}

	/**
	 * @param count the count to set
	 */
	public void SetCount(int count) {
		SetParam(Consts.CONST_COUNT, String.valueOf(count));
	}
}
