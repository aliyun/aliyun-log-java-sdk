/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.common;

import java.io.Serializable;

/**
 * The class used to present the result of log histogram status. For every log
 * histogram, it contains : from/to time range, query hit count and query
 * completed status.
 * 
 * @author sls_dev
 * 
 */
public class Histogram implements Serializable {
	private static final long serialVersionUID = -6482128132894559534L;
	public int mFromTime;
	public int mToTime;
	public long mCount;
	// a flag to determine if the query process is completed
	// if mIsCompleted = true, then the processing is completed, the return
	// result is precision, other wise, the return result is not precision
	public boolean mIsCompleted;

	/**
	 * 
	 * @param from
	 *            histogram begin time
	 * @param to
	 *            histogram end time
	 * @param count
	 *            histogram hit count
	 * @param processStatus
	 *            histogram query status(Complete or ImComplete)
	 */
	public Histogram(int from, int to, long count, String processStatus) {
		mFromTime = from;
		mToTime = to;
		mCount = count;
		if (processStatus.equals(Consts.CONST_RESULT_COMPLETE)) {
			mIsCompleted = true;
		} else {
			mIsCompleted = false;
		}
	}

	/**
	 * Get the histogram begin time
	 * 
	 * @return histogram begin time
	 */
	public int GetFrom() {
		return mFromTime;
	}

	/**
	 * Get the histogram end time
	 * 
	 * @return histogram end time
	 */
	public int GetTo() {
		return mToTime;
	}

	/**
	 * Get the histogram hit count
	 * 
	 * @return histogram hit count
	 */
	public long GetCount() {
		return mCount;
	}

	/**
	 * Check if the histogram is completed
	 * 
	 * @return if this histogram is completed
	 */
	public boolean IsCompleted() {
		return mIsCompleted;
	}
}
