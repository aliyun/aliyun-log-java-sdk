package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListMachineGroupResponse extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4626637425501553843L;
	protected int total  = 0;
	protected int count = 0;
	protected List<String> machineGroups = new ArrayList<String>();
	
	public ListMachineGroupResponse(Map<String, String> headers, int count, int total, List<String> machineGroups) {
		super(headers);
		this.total = total;
		this.count = count;
		SetMachineGroups(machineGroups);
	}

	public int GetTotal() {
		return total;
	}

	public int GetCount() {
		return count;
	}

	public List<String> GetMachineGroups() {
		return machineGroups;
	}

	private void SetMachineGroups(List<String> machineGroups) {
		this.machineGroups = new ArrayList<String>();
		for (String machineGroup:machineGroups) {
			this.machineGroups.add(machineGroup);
		}
	}
}
