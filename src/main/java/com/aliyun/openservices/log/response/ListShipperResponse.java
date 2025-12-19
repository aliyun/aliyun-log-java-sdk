package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListShipperResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7254330345146275069L;

	private int count = 0;
	private int total = 0;
	private List<String> shippers = new ArrayList<String>();

	public ListShipperResponse(Map<String, String> headers, int count,
			int total, List<String> shippers) {
		super(headers);
		this.count = count;
		this.total = total;
		this.shippers = new ArrayList<String>(shippers);
	}

	public int GetCount() {
		return count;
	}

	public int GetTotal() {
		return total;
	}

	public List<String> GetShippers() {
		return new ArrayList<String>(shippers);
	}

}
