package com.aliyun.openservices.log.request;

public class ClearLogStoreStorageRequest extends Request {
	private static final long serialVersionUID = -5790412798012257335L;
	private String logStoreName;
	
	public ClearLogStoreStorageRequest(String project, String logStoreName) {
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
