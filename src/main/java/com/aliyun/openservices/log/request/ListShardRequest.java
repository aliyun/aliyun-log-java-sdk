package com.aliyun.openservices.log.request;

public class ListShardRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1255174831216721645L;
	protected String mLogStore = "";
	
	public ListShardRequest(String project, String logStore) {
		super(project);
		mLogStore = logStore;
	}

	public String GetLogStore() {
		return mLogStore;
	}
	public void SetLogStore(String logStore) {
		mLogStore = logStore;
	}
}
