package com.aliyun.openservices.log.request;

public class GetSavedSearchRequest extends Request {

	private static final long serialVersionUID = -8238819580010798983L;
	protected String savedSearchName;

	public GetSavedSearchRequest(String project, String savedSearchName) {
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
