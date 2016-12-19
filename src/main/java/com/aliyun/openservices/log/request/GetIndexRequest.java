package com.aliyun.openservices.log.request;

public class GetIndexRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8948963766179055635L;
	private String logStore;
	
	public GetIndexRequest(String project, String logStore) {
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
