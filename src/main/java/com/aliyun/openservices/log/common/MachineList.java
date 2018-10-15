package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.util.Args;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

public class MachineList implements Serializable {

	private static final long serialVersionUID = -1098681309764178588L;
	private ArrayList<String> machineList;

	public MachineList() {
		this.machineList = new ArrayList<String>();
	}
	
	public MachineList(ArrayList<String> machineList) {
		SetMachineList(machineList);
	}

	public MachineList(MachineList machineList) {
		Args.notNull(machineList, "machineList");
		SetMachineList(machineList.GetMachineList());
	}

	public ArrayList<String> GetMachineList(){
		return machineList;
	}

	public void SetMachineList(ArrayList<String> machineList) {
		this.machineList = new ArrayList<String>(machineList);
	}

	public void SetMachineList(JSONArray machineListJSONArray) {
		machineList = new ArrayList<String>();
		for(int i = 0;i < machineListJSONArray.size();i++) {
			String machine = machineListJSONArray.getString(i);
			machineList.add(machine);
		}
	}

	private JSONArray ToRequestJson() {
		JSONArray machineList = new JSONArray();
		machineList.addAll(GetMachineList());
		return machineList;
	}
	
	public String ToRequestString() {
		return ToRequestJson().toString();
	}

	public void FromJsonArray(JSONArray array) throws LogException {
		try {
			SetMachineList(array);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateMachineList", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String machineListString) throws LogException {
		try {
			JSONArray machineArray = JSONArray.fromObject(machineListString);
			FromJsonArray(machineArray);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateMachineGroup", e.getMessage(), e, "");
		}
	}
}
