package com.aliyun.openservices.log.common;

import java.io.Serializable;

import com.aliyun.openservices.log.exception.LogException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Alert implements Serializable {

	private static final long serialVersionUID = 1860692437007431325L;
	protected String alertName;
	protected String savedSearchName;
	protected String from;
	protected String to;
	protected int checkInterval;
	protected int count;
	protected String alertKey;
	protected String alertValue;
	protected String comparator;
	protected String actionType;
	protected String phoneNumber;
	protected String roleArn;
	protected String mnsParam;
	protected String message;
	protected String webhook;

	public String getWebhook() { return webhook;}
	public void setWebhook(String webhook) { this.webhook = webhook; }
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMnsParam() {
		return mnsParam;
	}
	public void setMnsParam(String mnsParam) {
		this.mnsParam = mnsParam;
	}
	public String getRoleArn() {
		return roleArn;
	}
	public void setRoleArn(String roleArn) {
		this.roleArn = roleArn;
	}
	public String getAlertName() {
		return alertName;
	}
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
	public String getSavedSearchName() {
		return savedSearchName;
	}
	public void setSavedSearchName(String savedSearchName) {
		this.savedSearchName = savedSearchName;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public int getCheckInterval() {
		return checkInterval;
	}
	public void setCheckInterval(int checkInterval) {
		this.checkInterval = checkInterval;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getAlertKey() {
		return alertKey;
	}
	public void setAlertKey(String alertKey) {
		this.alertKey = alertKey;
	}
	public String getAlertValue() {
		return alertValue;
	}
	public void setAlertValue(String alertValue) {
		this.alertValue = alertValue;
	}
	public String getComparator() {
		return comparator;
	}
	public void setComparator(String comparator) {
		this.comparator = comparator;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Alert() {}
	public Alert(Alert alert) {
		super();
		this.alertName = alert.alertName;
		this.savedSearchName = alert.savedSearchName;
		this.roleArn = alert.roleArn;
		this.from = alert.from;
		this.to = alert.to;
		this.checkInterval = alert.checkInterval;
		this.count = alert.count;
		this.alertKey  = alert.alertKey;
		this.alertValue = alert.alertValue;
		this.comparator = alert.comparator;
		this.actionType = alert.actionType;
		this.phoneNumber = alert.phoneNumber;
	}
	public JSONObject ToJsonObject() {
		JSONObject alertJson = new JSONObject();
		alertJson.put(Consts.CONST_ALERT_NAME, getAlertName());
		alertJson.put(Consts.CONST_ALERT_ROLEARN, getRoleArn());
		alertJson.put(Consts.CONST_ALERT_SAVEDSEARCHNAME, getSavedSearchName());
		alertJson.put(Consts.CONST_ALERT_FROM, getFrom());
		alertJson.put(Consts.CONST_ALERT_TO, getTo());
		alertJson.put(Consts.CONST_ALERT_CHECKINTERVAL, getCheckInterval());
		alertJson.put(Consts.CONST_ALERT_COUNT, getCount());
		JSONObject alertDetail = new JSONObject();
		alertDetail.put(Consts.CONST_ALERT_KEY, getAlertKey());
		alertDetail.put(Consts.CONST_ALERT_VALUE, getAlertValue());
		alertDetail.put(Consts.CONST_ALERT_COMPARATOR, getComparator());
		alertJson.put(Consts.CONST_ALERT_DETAIL, alertDetail);
		alertJson.put(Consts.CONST_ALERT_ACTIONTYPE, getActionType());
		JSONObject actionDetail = new JSONObject();
		if (getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_SMS))
			actionDetail.put(Consts.CONST_ALERT_ACTIONDETAIL_PHONENUMBER, getPhoneNumber());
		else if (getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_MNS))
			actionDetail.put(Consts.CONST_ALERT_ACTIONDETAIL_MNS_PARAM, getMnsParam());
		else if (getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_DINGTALK) || getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_DINGTALK)) {
			actionDetail.put(Consts.CONST_ALERT_ACTIONDETAIL_MESSAGE, getMessage());
			actionDetail.put(Consts.CONST_ALERT_ACTIONDETAIL_WEBHOOK, getWebhook());
		}
		else
			actionDetail.put(Consts.CONST_ALERT_ACTIONDETAIL_MESSAGE, getMessage());
		alertJson.put(Consts.CONST_ALERT_ACTIONDETAIL, actionDetail);
		return alertJson;
	}
	public String ToJsonString() {
		return ToJsonObject().toString();
	}
	public void FromJsonObject(JSONObject dict) throws LogException {
		try {		
			setAlertName(dict.getString(Consts.CONST_ALERT_NAME));
			setRoleArn(dict.getString(Consts.CONST_ALERT_ROLEARN));
			setSavedSearchName(dict.getString(Consts.CONST_ALERT_SAVEDSEARCHNAME));
			setFrom(dict.getString(Consts.CONST_ALERT_FROM));
			setTo(dict.getString(Consts.CONST_ALERT_TO));
			setCheckInterval(dict.getInt(Consts.CONST_ALERT_CHECKINTERVAL));
			setCount(dict.getInt(Consts.CONST_ALERT_COUNT));
			JSONObject alertDetail = dict.getJSONObject(Consts.CONST_ALERT_DETAIL);
			setAlertKey(alertDetail.getString(Consts.CONST_ALERT_KEY));
			setAlertValue(alertDetail.getString(Consts.CONST_ALERT_VALUE));
			setComparator(alertDetail.getString(Consts.CONST_ALERT_COMPARATOR));
			setActionType(dict.getString(Consts.CONST_ALERT_ACTIONTYPE));
			JSONObject actionDetail = dict.getJSONObject(Consts.CONST_ALERT_ACTIONDETAIL);
			if (getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_SMS))
				setPhoneNumber(actionDetail.getString(Consts.CONST_ALERT_ACTIONDETAIL_PHONENUMBER));
			else if (getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_MNS))
				setMnsParam(actionDetail.getString(Consts.CONST_ALERT_ACTIONDETAIL_MNS_PARAM));
			else if (getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_WEBHOOK) || getActionType().equals(Consts.CONST_ALERT_ACTIONTYPE_DINGTALK)) {
				setMessage(actionDetail.getString(Consts.CONST_ALERT_ACTIONDETAIL_MESSAGE));
				setWebhook(actionDetail.getString(Consts.CONST_ALERT_ACTIONDETAIL_WEBHOOK));
			}
			else
				setMessage(actionDetail.getString(Consts.CONST_ALERT_ACTIONDETAIL_MESSAGE));
			
		} catch (JSONException e) {
			throw new LogException("FailToGenerateAlert",  e.getMessage(), e, "");
		}
	}
	public void FromJsonString(String alertString) throws LogException {
		try {
			JSONObject dict = JSONObject.fromObject(alertString);
			FromJsonObject(dict);
		} catch (JSONException e) {
			throw new LogException("FailToGenerateAlert", e.getMessage(), e, "");
		}
	}
}
