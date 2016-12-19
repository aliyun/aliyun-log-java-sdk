package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.exception.LogException;

public class UpdateMachineGroupRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5965524022645169368L;
	protected MachineGroup machineGroup = new MachineGroup();

	public UpdateMachineGroupRequest(String project, MachineGroup machineGroup) throws LogException {
		super(project);
		SetMachineGroup(machineGroup);
	}

	public MachineGroup GetMachineGroup() {
		return machineGroup;
	}

	public void SetMachineGroup(MachineGroup machineGroup) throws LogException {
		this.machineGroup = new MachineGroup(machineGroup);
	}
	
}
