package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class AlertFail implements Serializable{

	private static final long serialVersionUID = -9130780797633742909L;
	protected Long failTime;
	protected boolean triggerStatus;
	protected String triggerMessage;
	protected boolean actionStatus;
	protected String actionMessage;
	public Long getFailTime() {
		return failTime;
	}
	public void setFailTime(Long failTime) {
		this.failTime = failTime;
	}
	public boolean isTriggerStatus() {
		return triggerStatus;
	}
	public void setTriggerStatus(boolean triggerStatus) {
		this.triggerStatus = triggerStatus;
	}
	public String getTriggerMessage() {
		return triggerMessage;
	}
	public void setTriggerMessage(String triggerMessage) {
		this.triggerMessage = triggerMessage;
	}
	public boolean isActionStatus() {
		return actionStatus;
	}
	public void setActionStatus(boolean actionStatus) {
		this.actionStatus = actionStatus;
	}
	public String getActionMessage() {
		return actionMessage;
	}
	public void setActionMessage(String actionMessage) {
		this.actionMessage = actionMessage;
	}

	public AlertFail() {}
	public AlertFail(AlertFail alertFail) {
		super();
		this.failTime = alertFail.failTime;
		this.triggerStatus = alertFail.triggerStatus;
		this.triggerMessage = alertFail.triggerMessage;
		this.actionStatus = alertFail.actionStatus;
		this.actionMessage = alertFail.actionMessage;
	}
	public JSONObject ToJsonObject() {
		JSONObject alertFailJson = new JSONObject();
		alertFailJson.put(Consts.CONST_ALERTTIME, getFailTime());
		alertFailJson.put(Consts.CONST_TRIGGERSTATUS, isTriggerStatus());
		alertFailJson.put(Consts.CONST_TRIGGERMESSAGE, getTriggerMessage());
		alertFailJson.put(Consts.CONST_ACTIONSTATUS, isActionStatus());
		alertFailJson.put(Consts.CONST_ACTIONMESSAGE, getActionMessage());
		return alertFailJson;
	}
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {
			setFailTime(dict.getLong(Consts.CONST_ALERTTIME));
			setTriggerStatus(dict.getBoolean(Consts.CONST_TRIGGERSTATUS));
			setTriggerMessage(dict.getString(Consts.CONST_TRIGGERMESSAGE));
			setActionStatus(dict.getBoolean(Consts.CONST_ACTIONSTATUS));
			setActionMessage(dict.getString(Consts.CONST_ACTIONMESSAGE));
		} catch (JSONException e) {
			throw new LogException("FailToGenerateAlertFail",  e.getMessage(), e, "");
		}
	}
	public void FromJsonString(String alertFailString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(alertFailString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateAlertFail", e.getMessage(), e, "");
		}
	}
}
