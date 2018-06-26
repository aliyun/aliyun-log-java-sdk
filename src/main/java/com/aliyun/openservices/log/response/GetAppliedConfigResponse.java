package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Map;

public class GetAppliedConfigResponse extends Response {

	private static final long serialVersionUID = -9132526915584919104L;

	private ArrayList<String> configs;

	public GetAppliedConfigResponse(Map<String, String> headers, ArrayList<String> group) {
		super(headers);
		SetConfigs(group);
	}

	public ArrayList<String> Getconfigs() {
		return configs;
	}

	public void SetConfigs(ArrayList<String> configs) {
		this.configs = new ArrayList<String>(configs);
	}

	public int GetTotal() {
		return configs.size();
	}

}
