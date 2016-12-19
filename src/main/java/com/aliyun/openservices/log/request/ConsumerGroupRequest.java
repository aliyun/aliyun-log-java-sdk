package com.aliyun.openservices.log.request;

public class ConsumerGroupRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6066493468935882604L;
	private String logStore;
	public ConsumerGroupRequest(String project, String logstore) {
		super(project);
		this.logStore = logstore;
	}
	public String GetLogStore() {
		return logStore;
	}
	public void SetLogStore(String logStore) {
		this.logStore = logStore;
	}
	
}
