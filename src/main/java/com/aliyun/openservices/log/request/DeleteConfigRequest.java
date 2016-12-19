package com.aliyun.openservices.log.request;

public class DeleteConfigRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4289263714281922700L;
	protected String configName;
	
	public DeleteConfigRequest(String project, String configName) {
		super(project);
		this.configName = configName;
	}

	public String GetConfigName() {
		return configName;
	}

	public void SetConfigName(String groupName) {
		this.configName = groupName;
	}
	
}
