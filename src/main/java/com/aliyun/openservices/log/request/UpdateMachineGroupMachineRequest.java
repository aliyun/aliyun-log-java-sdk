package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MachineList;
import com.aliyun.openservices.log.exception.LogException;

public class UpdateMachineGroupMachineRequest extends Request {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6247404132403567448L;
	protected MachineList machineList = new MachineList();
	protected String groupName;

	public UpdateMachineGroupMachineRequest(String project, String groupName, MachineList machineList) throws LogException {
		super(project);
		SetGroupName(groupName);
		SetMachineList(machineList);
	}
	
	public void SetGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String GetGroupName() {
		return groupName;
	}

	public MachineList GetMachineList() {
		return machineList;
	}

	public void SetMachineList(MachineList machineList) throws LogException {
		this.machineList = new MachineList(machineList);
	}
}
