package com.aliyun.openservices.log.request;

public class DeleteMachineGroupRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4848070566487574004L;
	protected String groupName = "";
	
	public DeleteMachineGroupRequest(String project, String groupName) {
		super(project);
		this.groupName = groupName;
	}

	public String GetGroupName() {
		return groupName;
	}

	public void SetGroupName(String groupName) {
		this.groupName = groupName;
	}
}
