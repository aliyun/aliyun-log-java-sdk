
package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.Map;

import com.aliyun.openservices.log.common.Config; 
import com.aliyun.openservices.log.exception.LogException;

public class GetAppliedMachineGroupsResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9132526915584919104L;
	ArrayList<String> machineGroups = new ArrayList<String> ();

	public GetAppliedMachineGroupsResponse(Map<String, String> headers, ArrayList<String> group) throws LogException {
		super(headers);
		SetMachineGroups(group);
	}

	public ArrayList<String> GetMachineGroups() {
		return machineGroups;
	}

	public void SetMachineGroups(ArrayList<String>  machineGroups) throws LogException {
		this.machineGroups = new ArrayList<String>(machineGroups);
	}
	public int GetTotal()
	{
		return machineGroups.size();
	}
	
}
