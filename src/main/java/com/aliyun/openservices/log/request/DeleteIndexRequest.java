package com.aliyun.openservices.log.request;

public class DeleteIndexRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3179846341386602249L;
	private String logStore;
	
	public DeleteIndexRequest(String project, String logStore) {
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
