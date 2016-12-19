package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.MachineGroup;
import com.aliyun.openservices.log.exception.LogException;

public class GetMachineGroupResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9132526915584919104L;
	MachineGroup machineGroup = new MachineGroup();

	public GetMachineGroupResponse(Map<String, String> headers, MachineGroup machineGroup) throws LogException {
		super(headers);
		SetMachineGroup(machineGroup);
	}

	public MachineGroup GetMachineGroup() {
		return machineGroup;
	}

	public void SetMachineGroup(MachineGroup machineGroup) throws LogException {
		this.machineGroup = new MachineGroup(machineGroup);
	}
	
}
