package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Config;

public class UpdateConfigRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1207531192749308813L;
	protected Config config = new Config();
	
	public UpdateConfigRequest(String project, Config config) {
		super(project);
		SetConfig(config);
	}

	public Config GetConfig() {
		return config;
	}

	public void SetConfig(Config config) {
		this.config = new Config(config);
	}
}
