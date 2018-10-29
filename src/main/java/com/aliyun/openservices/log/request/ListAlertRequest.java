package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListAlertRequest extends Request {

	private static final long serialVersionUID = 2857657915141931959L;

	public ListAlertRequest(String project) {
		super(project);
		setDisplayName("");
		setAlertName(Consts.DEFAULT_REQUEST_PARAM_ALERTNAME);
		setOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		setSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}
	
	public ListAlertRequest(String project, int offset, int size) {
		super(project);
		setDisplayName("");
		setAlertName(Consts.DEFAULT_REQUEST_PARAM_ALERTNAME);
		setOffset(offset);
		setSize(size);
	}
	
	public ListAlertRequest(String project, String alertName, int offset, int size) {
		super(project);
		setAlertName(alertName);
		setDisplayName("");
		setOffset(offset);
		setSize(size);
	}

	public ListAlertRequest(String project, String alertName, String displayName, int offset, int size) {
		super(project);
		setAlertName(alertName);
		setDisplayName(displayName);
		setOffset(offset);
		setSize(size);
	}
	
	public String getDisplayName() {
		return GetParam(Consts.CONST_DISPLAY_NAME);
	}
	
	public void setDisplayName(String displayName) {
		SetParam(Consts.CONST_DISPLAY_NAME, displayName);
	}
	
	public String getAlertName() {
		return GetParam(Consts.CONST_ALERT_NAME);
	}

	public void setAlertName(String alertName) {
		SetParam(Consts.CONST_ALERT_NAME, alertName);
	}

	public int getOffset() {
		return Integer.parseInt(GetParam(Consts.CONST_OFFSET));
	}

	public void setOffset(int offset) {
		SetParam(Consts.CONST_OFFSET, String.valueOf(offset));
	}

	public int getSize() {
		return Integer.parseInt(GetParam(Consts.CONST_SIZE));
	}

	public void setSize(int size) {
		SetParam(Consts.CONST_SIZE, String.valueOf(size));
	}
}
