/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.Consts;

/**
 * The response of the GetHistogram API from sls server
 * 
 * @author sls_dev
 * 
 */
public class GetHistogramsResponse extends Response {

	private static final long serialVersionUID = 5169979404935069850L;
	private boolean mIsCompleted = false;
	private long mCount = 0;
	private ArrayList<Histogram> mHistogram = new ArrayList<Histogram>();

	/**
	 * Construct a histogram response
	 * 
	 * @param headers
	 *            http headers
	 */
	public GetHistogramsResponse(Map<String, String> headers) {
		super(headers);
		this.SetProcessStatus(headers.get(Consts.CONST_X_SLS_PROCESS));
	}

	/**
	 * Get query hit count
	 * 
	 * @return hit count
	 */
	public long GetTotalCount() {
		return mCount;
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
	 * Add a time range histogram to the response
	 * 
	 * @param histogram
	 *            a time range histogram
	 */
	public void AddHistogram(Histogram histogram) {
		mHistogram.add(histogram);
		mCount += histogram.GetCount();
	}

	/**
	 * Set all the time range histogram to the response
	 * 
	 * @param histograms
	 *            all time range histogram
	 */
	public void SetHistograms(List<Histogram> histograms) {
		mHistogram = new ArrayList<Histogram>(histograms);
	}

	/**
	 * Get all the time range histogram from the response
	 * 
	 * @return all time range histogram
	 */
	public ArrayList<Histogram> GetHistograms() {
		return mHistogram;
	}

}
