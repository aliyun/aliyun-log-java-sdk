package com.aliyun.openservices.log.request;

public class GetLogStoreRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8932189024638798559L;
	private String logStore;
	
	public GetLogStoreRequest(String project, String logStore) {
		super(project);
		this.logStore = logStore;
	}

	/**
	 * @return the logStore
	 */
	public String GetLogStore() {
		return logStore;
	}

	/**
	 * @param logStore the logStore to set
	 */
	public void SetLogStore(String logStore) {
		this.logStore = logStore;
	}
}
