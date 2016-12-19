package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Map;

import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.exception.LogException;

public class GetAppliedConfigResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9132526915584919104L;
	ArrayList<String> configs = new ArrayList<String>();

	public GetAppliedConfigResponse(Map<String, String> headers,
			ArrayList<String> group) throws LogException {
		super(headers);
		SetConfigs(group);
	}

	public ArrayList<String> Getconfigs() {
		return configs;
	}

	public void SetConfigs(ArrayList<String> configs) throws LogException {
		this.configs = new ArrayList<String>(configs);
	}

	public int GetTotal() {
		return configs.size();
	}

}
