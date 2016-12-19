package com.aliyun.openservices.log.request;

public class GetAppliedMachineGroupRequest extends Request {

	protected String configName = "";
	public GetAppliedMachineGroupRequest(String project, String configName) {
		super(project);
		// TODO Auto-generated constructor stub
		this.configName = configName;
	}
	public String GetConfigName() {
		return configName;
	}
	public void SetConfigName(String configName) {
		this.configName = configName;
	}

}
