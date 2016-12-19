package com.aliyun.openservices.log.request;

public class DeleteLogStoreRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2597932397395479932L;
	protected String logStoreName;
	
	public DeleteLogStoreRequest(String project, String logStoreName) {
		super(project);
		this.logStoreName = logStoreName;
	}

	public String GetLogStoreName() {
		return logStoreName;
	}

	public void SetLogStoreName(String logStoreName) {
		this.logStoreName = logStoreName;
	}
}
