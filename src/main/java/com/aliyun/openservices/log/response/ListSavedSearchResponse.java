package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.SavedSearch;

public class ListSavedSearchResponse extends Response {

	private static final long serialVersionUID = -6859512318225405051L;
	protected int count = 0;
	protected int total = 0;
	protected List<SavedSearch> savedSearches = new ArrayList<SavedSearch>();

	public ListSavedSearchResponse(Map<String, String> headers, int count, int total, List<SavedSearch> savedSearches) {
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

	public List<SavedSearch> getSavedSearches() {
		return savedSearches;
	}

	public void setSavedSearches(List<SavedSearch> savedSearches) {
		this.savedSearches = new ArrayList<SavedSearch>();
		for (SavedSearch savedSearch : savedSearches) {
			savedSearches.add(savedSearch);
		}
	}

}