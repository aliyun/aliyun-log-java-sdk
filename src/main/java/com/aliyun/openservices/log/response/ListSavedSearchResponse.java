package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListSavedSearchResponse extends Response {

	private static final long serialVersionUID = -6859512318225405051L;
	protected int count = 0;
	protected int total = 0;
	protected List<String> savedSearches = new ArrayList<String>();

	public ListSavedSearchResponse(Map<String, String> headers, int count, int total, List<String> savedSearches) {
		super(headers);
		setCount(count);
		setTotal(total);
		this.savedSearches = savedSearches;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<String> getSavedSearches() {
		return savedSearches;
	}

	public void setSavedSearches(List<String> savedSearches) {
		this.savedSearches = new ArrayList<String>();
		for (String savedSearch : savedSearches) {
			savedSearches.add(savedSearch);
		}
	}

}