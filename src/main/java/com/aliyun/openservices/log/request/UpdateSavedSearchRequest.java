package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.SavedSearch;

public class UpdateSavedSearchRequest extends Request {

	private static final long serialVersionUID = -568170941814690043L;
	protected SavedSearch savedSearch;

	public UpdateSavedSearchRequest(String project, SavedSearch savedSearch) {
		super(project);
		this.savedSearch = savedSearch;
	}

	public SavedSearch getSavedSearch() {
		return savedSearch;
	}

	public void setSavedSearch(SavedSearch savedSearch) {
		this.savedSearch = savedSearch;
	}
}
