/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.common.Consts;

/**
 * The response of the GetLog API from sls server
 * 
 * @author sls_dev
 * 
 */
public class GetLogsResponse extends Response {

	private static final long serialVersionUID = -7866328557378599379L;

	private boolean mIsCompleted = false;

	private ArrayList<QueriedLog> mLogs = new ArrayList<QueriedLog>();

	/**
	 * Construct the response with http headers
	 * 
	 * @param headers
	 *            http headers
	 */
	public GetLogsResponse(Map<String, String> headers) {
		super(headers);
		this.SetProcessStatus(headers.get(Consts.CONST_X_SLS_PROCESS));

	}

	/**
	 * Set process status to the response
	 * 
	 * @param processStatus
	 *            process status(Complete/InComplete only)
	 */
	public void SetProcessStatus(String processStatus) {
		if (processStatus.equals(Consts.CONST_RESULT_COMPLETE)) {
			mIsCompleted = true;
		} else {
			mIsCompleted = false;
		}

	}

	/**
	 * Check if the GetHistogram is completed
	 * 
	 * @return true if the query is complete in the sls server
	 */
	public boolean IsCompleted() {
		return mIsCompleted;
	}

	/**
	 * Set all the log data to the response
	 * 
	 * @param logs
	 *            log datas
	 */
	public void SetLogs(List<QueriedLog> logs) {
		mLogs = new ArrayList<QueriedLog>(logs);
	}

	/**
	 * Add one log to the response
	 * 
	 * @param log
	 *            log data to add
	 */
	public void AddLog(QueriedLog log) {
		mLogs.add(log);
	}

	/**
	 * Get all logs from the response
	 * 
	 * @return all log data
	 */
	public ArrayList<QueriedLog> GetLogs() {
		return mLogs;
	}

	/**
	 * Get log number from the response
	 * 
	 * @return log number
	 */
	public int GetCount() {
		return mLogs.size();
	}

}
