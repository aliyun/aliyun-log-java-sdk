package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ShipperTask implements JsonDeserializable {

	private String mTaskId;
	private String mTaskStatus;
	private String mTaskMessage;
	private int mTaskCreateTime;
	private int mTaskLastDataReceiveTime;
	private int mTaskFinishTime;
	private long mTaskDataLines;

	public ShipperTask(String taskId, String taskStatus, String taskMessage,
			int taskCreateTime, int taskLastDataReceiveTime, int taskFinishTime) {
		mTaskId = taskId;
		mTaskStatus = taskStatus;
		mTaskCreateTime = taskCreateTime;
		mTaskLastDataReceiveTime = taskLastDataReceiveTime;
		mTaskFinishTime = taskFinishTime;
	}
	
	public ShipperTask(String taskId, String taskStatus, String taskMessage,
			int taskCreateTime, int taskLastDataReceiveTime, int taskFinishTime, long taskDataLines) {
		mTaskId = taskId;
		mTaskStatus = taskStatus;
		mTaskCreateTime = taskCreateTime;
		mTaskLastDataReceiveTime = taskLastDataReceiveTime;
		mTaskFinishTime = taskFinishTime;
		mTaskDataLines = taskDataLines;
	}

	public ShipperTask() {
		
	}
	
	public Long GetTaskDataLines() {
		return mTaskDataLines;
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

	@InternalApi
	public void fromJsonObject(JSONObject obj) {
		mTaskId = obj.getString("id");
		mTaskStatus = obj.getString("taskStatus");
		mTaskMessage = obj.getString("taskMessage");
		mTaskCreateTime = obj.getIntValue("taskCreateTime");
		mTaskLastDataReceiveTime = obj.getIntValue("taskLastDataReceiveTime");
		mTaskFinishTime = obj.getIntValue("taskFinishTime");
		mTaskDataLines = obj.getLong("taskDataLines");
	}
	
}
