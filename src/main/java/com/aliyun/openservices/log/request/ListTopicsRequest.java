/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

/**
 * The request used to list topic from sls server
 * 
 * @author sls_dev
 * 
 */
public class ListTopicsRequest extends Request {

	private static final long serialVersionUID = 6465949997077025076L;

	public String mLogStore;

	/**
	 * Construct a request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store of the project
	 */
	public ListTopicsRequest(String project, String logStore) {
		super(project);
		mLogStore = logStore;
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_TOPIC);
	}

	/**
	 * Construct a request
	 * 
	 * @param project
	 *            project name
	 * @param logStore
	 *            log store of the project
	 * @param token
	 *            the start topic token
	 * @param line
	 *            how many topics to get, the max topic count is decided by
	 *            the sls backend server
	 */
	public ListTopicsRequest(String project, String logStore, String token,
			int line) {
		super(project);
		mLogStore = logStore;
		SetToken(token);
		SetLine(line);
		SetParam(Consts.CONST_TYPE, Consts.CONST_TYPE_TOPIC);
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
	 * Get log store name
	 * 
	 * @return log store name
	 */
	public String GetLogStore() {
		return mLogStore;
	}

	/**
	 * Set request token
	 * 
	 * @param token
	 *            topic token
	 */
	public void SetToken(String token) {
		SetParam(Consts.CONST_TOKEN, token);
	}

	/**
	 * Get request token
	 * 
	 * @return request token
	 */
	public String GetToken() {
		return GetParam(Consts.CONST_TOKEN);
	}

	/**
	 * Set request line
	 * 
	 * @param line
	 *            request line
	 */
	public void SetLine(int line) {
		SetParam(Consts.CONST_LINE, String.valueOf(line));
	}

	/**
	 * Get request line
	 * 
	 * @return request line
	 */
	public int GetLine() {
		String line = GetParam(Consts.CONST_LINE);
		if (line.isEmpty()) {
			return 0;
		} else {
			return Integer.parseInt(line);
		}
	}

}
