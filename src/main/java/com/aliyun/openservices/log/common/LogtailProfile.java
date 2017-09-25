package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class LogtailProfile implements Serializable {

	private static final long serialVersionUID = -6227423811861315096L;
	
	// Const 
	private static final String LOGTAILPROFILE_TIME = "__time__";
	private static final String LOGTAILPROFILE_SOURCE = "__source__";
	private static final String LOGTAILPROFILE_IP = "ip";
	private static final String LOGTAILPROFILE_ALARMCOUNT = "alarm_count";
	private static final String LOGTAILPROFILE_ALARMTYPE = "alarm_type";
	private static final String LOGTAILPROFILE_ALARMMESSAGE = "alarm_message";
	private static final String LOGTAILPROFILE_MACHINEOS = "os";
	
	private long time = 0;
	private String source = "";
	private String ip = "";
	private int alarmCount = 0;
	private String alarmType = "";
	private String alarmMessage = "";
	private String machineOS = "";
	
	public LogtailProfile() {
	}
	
	public LogtailProfile(long time, String source, String ip, int alarmCount, String alarmType, String alarmMessage, String machineOS) {
		this.time = time;
		this.source = source;
		this.ip = ip;
		this.alarmCount = alarmCount;
		this.alarmType = alarmType;
		this.alarmMessage = alarmMessage;
		this.machineOS = machineOS;
	}
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getAlarmCount() {
		return alarmCount;
	}
	public void setAlarmCount(int alarmCount) {
		this.alarmCount = alarmCount;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmMessage() {
		return alarmMessage;
	}
	public void setAlarmMessage(String alarmMessage) {
		this.alarmMessage = alarmMessage;
	}
	public String getMachineOS() {
		return machineOS;
	}
	public void setMachineOS(String machineOS) {
		this.machineOS = machineOS;
	}
	
	public JSONObject ToJsonObject() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(LOGTAILPROFILE_TIME, time);
		jsonObj.put(LOGTAILPROFILE_SOURCE, source);
		jsonObj.put(LOGTAILPROFILE_IP, ip);
		jsonObj.put(LOGTAILPROFILE_ALARMCOUNT, alarmCount);
		jsonObj.put(LOGTAILPROFILE_ALARMTYPE, alarmType);
		jsonObj.put(LOGTAILPROFILE_ALARMMESSAGE, alarmMessage);
		jsonObj.put(LOGTAILPROFILE_MACHINEOS, machineOS);

		return jsonObj;
	}
	
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			this.time = dict.getLong(LOGTAILPROFILE_TIME);
			this.source = dict.getString(LOGTAILPROFILE_SOURCE);
			this.ip = dict.getString(LOGTAILPROFILE_IP);
			this.alarmCount = dict.getInt(LOGTAILPROFILE_ALARMCOUNT);
			this.alarmType = dict.getString(LOGTAILPROFILE_ALARMTYPE);
			this.alarmMessage = dict.getString(LOGTAILPROFILE_ALARMMESSAGE);
			this.machineOS = dict.getString(LOGTAILPROFILE_MACHINEOS);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateLogtailProfile", e.getMessage(), e, "");
		}
	}
	
	public void FromJsonString(String logtailProfileString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(logtailProfileString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateLogtailProfile", e.getMessage(), e, "");
		}
	}
}
