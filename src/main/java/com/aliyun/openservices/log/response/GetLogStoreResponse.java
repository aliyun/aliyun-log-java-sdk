package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.LogStore;

public class GetLogStoreResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4265633972358905206L;
	
	LogStore logStore = new LogStore();

	public GetLogStoreResponse(Map<String, String> headers, LogStore logStore) {
		super(headers);
		SetLogStore(logStore);
	}

	public LogStore GetLogStore() {
		return logStore;
	}

	public void SetLogStore(LogStore logStore) {
		this.logStore = new LogStore(logStore);
	}
}
