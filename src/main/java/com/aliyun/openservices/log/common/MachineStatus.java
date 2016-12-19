package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class MachineStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5680708662484604126L;
	protected String binaryCurVersion = "";
	protected String binaryDeployVersion = "";
	
	public MachineStatus() {
	}
	
	public MachineStatus(String binaryCurVersion,
			String binaryDeployVersion) {
		super();
		this.binaryCurVersion = binaryCurVersion;
		this.binaryDeployVersion = binaryDeployVersion;
	}
	
	public MachineStatus(MachineStatus status) {
		super();
		this.binaryCurVersion = status.GetBinaryCurVersion();
		this.binaryDeployVersion = status.GetBinaryDeployVersion();
	}

	public String GetBinaryCurVersion() {
		return binaryCurVersion;
	}

	public void SetBinaryCurVersion(String binaryCurVersion) {
		this.binaryCurVersion = binaryCurVersion;
	}

	public String GetBinaryDeployVersion() {
		return binaryDeployVersion;
	}

	public void SetBinaryDeployVersion(String binaryDeployVersion) {
		this.binaryDeployVersion = binaryDeployVersion;
	}	
	
	public JSONObject ToJsonObject() {
		JSONObject object = new JSONObject();
		
		object.put("binaryCurVersion", binaryCurVersion);
		object.put("binaryDeployVersion", binaryDeployVersion);
		
		return object;
	}
	
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject statusDict) throws LogException {
		try {
			this.binaryCurVersion = statusDict.getString("binaryCurVersion");
			this.binaryDeployVersion = statusDict.getString("binaryDeployVersion");
		} catch (JSONException e) {
			throw new LogException("FailToGenerateMachineStatus", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String machineStatusString) throws LogException {
		try {
			JSONObject statusDict = JSONObject.fromObject(machineStatusString);
			FromJsonObject(statusDict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateMachineStatus", e.getMessage(), e, "");
		}
	}
}
