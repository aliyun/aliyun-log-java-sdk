package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Config;

public class GetConfigResponse extends Response {

	private static final long serialVersionUID = 3751875183016178970L;

	private Config config;

	public GetConfigResponse(Map<String, String> headers, Config config) {
		super(headers);
		SetConfig(config);
	}

	public Config GetConfig() {
		return config;
	}

	public void SetConfig(Config config) {
		this.config = new Config(config);
	}

}
