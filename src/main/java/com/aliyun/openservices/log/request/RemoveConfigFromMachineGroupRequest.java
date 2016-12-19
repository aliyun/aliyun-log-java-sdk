package com.aliyun.openservices.log.request;

public class RemoveConfigFromMachineGroupRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3972342020564849089L;
	protected String groupName = "";
	protected String configName = "";
	
	public RemoveConfigFromMachineGroupRequest(String project, String groupName, String configName) {
		super(project);
		this.groupName = groupName;
		this.configName = configName;
	}

	public String GetGroupName() {
		return groupName;
	}

	public void SetGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String GetConfigName() {
		return configName;
	}

	public void SetConfigName(String configName) {
		this.configName = configName;
	}
	
}
