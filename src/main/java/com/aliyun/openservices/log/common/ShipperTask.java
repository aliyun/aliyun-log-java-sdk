package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

public class ShipperTask {

	private String mTaskId;
	private String mTaskStatus;
	private String mTaskMessage;
	private int mTaskCreateTime;
	private int mTaskLastDataReceiveTime;
	private int mTaskFinishTime;

	public ShipperTask(String taskId, String taskStatus, String taskMessage,
			int taskCreateTime, int taskLastDataReceiveTime, int taskFinishTIme) {
		mTaskId = taskId;
		mTaskStatus = taskStatus;
		mTaskCreateTime = taskCreateTime;
		mTaskLastDataReceiveTime = taskLastDataReceiveTime;
		mTaskFinishTime = taskFinishTIme;
	}

	public ShipperTask()
	{
		
	}
	public String GetTaskId() {
		return mTaskId;
	}

	public String GetTaskStatus() {
		return mTaskStatus;
	}

	public String GetTaskMessage() {
		return mTaskMessage;
	}

	public int GetTaskCreateTime() {
		return mTaskCreateTime;
	}

	public int GetTaskLastDataReceiveTime() {
		return mTaskLastDataReceiveTime;
	}

	public int GetTaskFinishTime() {
		return mTaskFinishTime;
	}

	public void FromJsonObject(JSONObject obj) {
		mTaskId = obj.getString("id");
		mTaskStatus = obj.getString("taskStatus");
		mTaskMessage = obj.getString("taskMessage");
		mTaskCreateTime = obj.getInt("taskCreateTime");
		mTaskLastDataReceiveTime = obj.getInt("taskLastDataReceiveTime");
		mTaskFinishTime = obj.getInt("taskFinishTime");
	}
	
}
