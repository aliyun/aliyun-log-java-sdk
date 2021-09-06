package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * The config of a machine
 * 
 * @author log-service-dev
 * 
 */
public class Machine implements Serializable {
	private static final long serialVersionUID = 5945880426501816900L;
	protected String ip = "";
	protected String machine_unique_id = "";
	protected String user_defined_id = "";
	protected long heartbeat_time = 0;

	public Machine() {
	}

	/**
	 * Construct a machine
	 * 
	 * @param ip
	 *            machine ip
	 * @param machine_unique_id
	 *            machine unique id
	 * @param user_defined_id
	 *            machine user defined id
	 * @param heartbeat_time
	 *            machine last update time
	 */
	public Machine(String ip, String machine_unique_id, String user_defined_id,
			long heartbeat_time) {
		this.ip = ip;
		this.machine_unique_id = machine_unique_id;
		this.user_defined_id = user_defined_id;
		this.heartbeat_time = heartbeat_time;
	}

	/**
	 * Return the Jonsobject of the machine
	 * 
	 * @return the Jonsobject of the machine
	 */
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("ip", ip);
		jsonObj.put("machine-uniqueid", machine_unique_id);
		jsonObj.put("userdefined-id", user_defined_id);
		jsonObj.put("lastHeartbeatTime", heartbeat_time);

		return jsonObj;
	}

	/**
	 * Return the json string of the machine
	 * 
	 * @return the json string of the machine
	 */
	public String ToJsonString() {
		return ToJsonObject().toString();
	}

	/**
	 * Construct a machine from a json object
	 * 
	 * @param dict
	 *            the json object config
	 * @throws LogException
	 *             if any error happen
	 */
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			this.ip = dict.getString("ip");
			this.machine_unique_id = dict.getString("machine-uniqueid");
			this.user_defined_id = dict.getString("userdefined-id");

			this.heartbeat_time = dict.getIntValue("lastHeartbeatTime");
		} catch (JSONException e) {
			throw new LogException("FailToGenerateMachine", e.getMessage(), e,
					"");
		}
	}

	/**
	 * Construct a machine from a json string
	 * 
	 * @param machineString
	 *            the json string
	 * @throws LogException
	 *             if any error happen
	 */
	public void FromJsonString(String machineString) throws LogException {
		try {
			JSONObject dict = JSONObject.parseObject(machineString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateMachine", e.getMessage(), e,
					"");
		}
	}

	/**
	 * construct a machine
	 * 
	 * @param machine
	 *            exist machine
	 */
	public Machine(Machine machine) {
		this.ip = machine.ip;
		this.machine_unique_id = machine.machine_unique_id;
		this.user_defined_id = machine.user_defined_id;
		this.heartbeat_time = machine.heartbeat_time;
	}

	/**
	 * 
	 * @return machine ip
	 */
	public String GetIp() {
		return ip;
	}

	/**
	 * 
	 * @return machine unique id
	 */
	public String GetMachineUniqueId() {
		return this.machine_unique_id;
	}

	/**
	 * 
	 * @return user defined id
	 */
	public String GetUserDefinedId() {
		return this.user_defined_id;
	}

	/**
	 * 
	 * @return machine last update heart beat time
	 */
	public long GetLastHeartBeatTime() {
		return this.heartbeat_time;
	}

}
