package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.exception.LogException;

public class CreateMachineGroupRequest extends Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7129206233517638569L;
	protected MachineGroup machineGroup = new MachineGroup();

	public CreateMachineGroupRequest(String project, MachineGroup machineGroup) throws LogException {
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
