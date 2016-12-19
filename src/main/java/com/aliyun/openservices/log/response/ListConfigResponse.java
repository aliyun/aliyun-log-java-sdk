package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListConfigResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4384808355083772083L;
	protected int total = 0;
	protected int count = 0;
	protected List<String> configs = new ArrayList<String>();
	
	public ListConfigResponse(Map<String, String> headers, int count, int total, List<String> configs) {
		super(headers);
		this.total = total;
		this.count = count;
		SetConfigs(configs);
	}

	public int GetTotal() {
		return total;
	}


	public int GetCount() {
		return count;
	}

	public List<String> GetConfigs() {
		return configs;
	}

	private void SetConfigs(List<String> configs) {
		this.configs = new ArrayList<String>();
		for (String config:configs) {
			this.configs.add(config);
		}
	}
}
