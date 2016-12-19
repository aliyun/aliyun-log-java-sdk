package com.aliyun.openservices.log.request;

public class GetConfigRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 100221215890063211L;
	protected String configName = "";
	
	public GetConfigRequest(String project, String configName) {
		super(project);
		this.configName = configName;
	}
	public String GetConfigName() {
		return configName;
	}
	public void SetConfigName(String configName) {
		this.configName = configName;
	}
	
}
