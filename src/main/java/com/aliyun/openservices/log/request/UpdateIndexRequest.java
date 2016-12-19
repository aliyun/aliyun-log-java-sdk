package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Index;

public class UpdateIndexRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4206573886031519911L;
	private String logStore;
	private Index index;
	
	public UpdateIndexRequest(String project, String logStore, Index index) {
		super(project);
		this.logStore = logStore;
		SetIndex(index);
	}

	/**
	 * @return the logStore
	 */
	public String GetLogStore() {
		return logStore;
	}

	/**
	 * @return the index
	 */
	public Index GetIndex() {
		return index;
	}

	/**
	 * @param logStore the logStore to set
	 */
	public void SetLogStore(String logStore) {
		this.logStore = logStore;
	}

	/**
	 * @param index the index to set
	 */
	public void SetIndex(Index index) {
		this.index = new Index(index);
	}
}
