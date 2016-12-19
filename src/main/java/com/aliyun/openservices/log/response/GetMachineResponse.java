package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Machine;

public class GetMachineResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6169800365898110627L;
	Machine machine = new Machine();

	public GetMachineResponse(Map<String, String> headers, Machine machine) {
		super(headers);
		SetMachine(machine);
	}

	public Machine GetMachine() {
		return machine;
	}

	public void SetMachine(Machine machine) {
		this.machine = new Machine(machine);
	}
	
}
