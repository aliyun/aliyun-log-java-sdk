package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListMachineGroupRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5179972992302396388L;

	public ListMachineGroupRequest(String project) {
		super(project);
		SetGroupName(Consts.DEFAULT_REQUEST_PARAM_GROUPNAME);
		SetOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		SetSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}
	
	public ListMachineGroupRequest(String project, int offset, int size) {
		super(project);
		SetGroupName(Consts.DEFAULT_REQUEST_PARAM_GROUPNAME);
		SetOffset(offset);
		SetSize(size);
	}
	
	public ListMachineGroupRequest(String project, String groupName, int offset, int size) {
		super(project);
		SetGroupName(groupName);
		SetOffset(offset);
		SetSize(size);
	}

	public String GetGroupName() {
		return GetParam(Consts.CONST_GROUPNAME);
	}

	public void SetGroupName(String groupName) {
		SetParam(Consts.CONST_GROUPNAME, groupName);
	}

	public int GetOffset() {
		return Integer.parseInt(GetParam(Consts.CONST_OFFSET));
	}

	public void SetOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	public int GetSize() {
		return Integer.parseInt(GetParam(Consts.CONST_SIZE));
	}

	public void SetSize(int size) {
		SetParam(Consts.CONST_SIZE, String.valueOf(size));
	}
	
}
