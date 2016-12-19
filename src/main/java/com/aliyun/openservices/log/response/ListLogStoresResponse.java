/*
 * Copyright (C) Alibaba Cloud Computing All rights reserved.
 */
package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The response of the ListLogStore API from sls server
 * 
 * @author sls_dev
 * 
 */
public class ListLogStoresResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4382847396623177901L;
	private ArrayList<String> mLogStores = new ArrayList<String>();
	private int mTotal;
	/**
	 * Construct the response with http headers
	 * 
	 * @param headers
	 *            http headers
	 */
	public ListLogStoresResponse(Map<String, String> headers) {
		super(headers);

	}

	/**
	 * Get log store number from the response
	 * 
	 * @return log store count 
	 */
	public int GetCount() {
		return mLogStores.size();
	}

	/**
	 * Set log stores to the response
	 * 
	 * @param logStores
	 *            log stores
	 */
	public void SetLogStores(List<String> logStores) {
		mLogStores = new ArrayList<String>(logStores);
	}

	/**
	 * Get all the log stores from the response
	 * 
	 * @return log stores
	 */
	public ArrayList<String> GetLogStores() {
		return mLogStores;
	}

	public int GetTotal() {
		return mTotal;
	}

	public void SetTotal(int mTotal) {
		this.mTotal = mTotal;
	}
}
