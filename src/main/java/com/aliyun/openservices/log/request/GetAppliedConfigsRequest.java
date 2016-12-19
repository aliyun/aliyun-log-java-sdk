package com.aliyun.openservices.log.request;

public class GetAppliedConfigsRequest extends Request {

	protected String groupName = "";
	public GetAppliedConfigsRequest(String project, String groupName) {
		super(project);
		// TODO Auto-generated constructor stub
		this.groupName = groupName;
	}
	public String GetGroupName() {
		return groupName;
	}
	public void SetGroupName(String groupName) {
		this.groupName = groupName;
	}

}
