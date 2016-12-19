package com.aliyun.openservices.log.request;

public class DeleteSavedSearchRequest extends Request {

	private static final long serialVersionUID = -5284289043463063743L;
	protected String savedSearchName;

	public DeleteSavedSearchRequest(String project, String savedSearchName) {
		super(project);
		this.savedSearchName = savedSearchName;
	}

	public String getSavedSearchName() {
		return savedSearchName;
	}

	public void setSavedSearchName(String savedSearchName) {
		this.savedSearchName = savedSearchName;
	}

}
