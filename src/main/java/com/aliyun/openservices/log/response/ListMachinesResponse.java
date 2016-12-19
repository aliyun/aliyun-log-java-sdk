package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Machine;

public class ListMachinesResponse  extends Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4626637425501553843L;
	protected int total = 0;
	protected int count = 0;
	protected List<Machine> machines = new ArrayList<Machine>();
	
	public ListMachinesResponse(Map<String, String> headers, int count, int total, List<Machine> machines) {
		super(headers);
		this.total = total;
		this.count = count;
		SetMachines(machines);
	}

	public int GetTotal() {
		return total;
	}

	public int GetCount() {
		return count;
	}

	public List<Machine> GetMachines() {
		return machines;
	}

	private void SetMachines(List<Machine> machines) {
		this.machines = new ArrayList<Machine>(machines);
	}

}
