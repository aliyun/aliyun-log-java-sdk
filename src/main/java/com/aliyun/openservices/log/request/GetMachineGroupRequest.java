package com.aliyun.openservices.log.request;

public class GetMachineGroupRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = -177034582073126856L;
	protected String groupName = "";
	
	public GetMachineGroupRequest(String project, String groupName) {
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
