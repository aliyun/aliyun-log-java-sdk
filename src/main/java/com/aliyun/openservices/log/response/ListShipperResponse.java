package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListShipperResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7254330345146275069L;

	private int mCount = 0;
	private int mTotal = 0;
	private List<String> mShippers = new ArrayList<String>();

	public ListShipperResponse(Map<String, String> headers, int count,
			int total, List<String> shippers) {
		super(headers);
		mCount = count;
		mTotal = total;
		mShippers = new ArrayList<String>(shippers);
	}

	public int GetCount() {
		return mCount;
	}

	public int GetTotal() {
		return mTotal;
	}

	public List<String> GetShippers() {
		return new ArrayList<String>(mShippers);
	}

}
