package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Config;

public class CreateConfigRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4809557839789600060L;
	protected Config config = new Config();

	public CreateConfigRequest(String project, Config config) {
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
