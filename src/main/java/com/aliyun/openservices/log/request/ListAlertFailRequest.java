package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Consts;

public class ListAlertFailRequest extends Request {

	private static final long serialVersionUID = -518289636743072650L;

	public ListAlertFailRequest(String project) {
		super(project);
		setAlertName(Consts.DEFAULT_REQUEST_PARAM_ALERTNAME);
		setOffset(Consts.DEFAULT_REQUEST_PARAM_OFFSET);
		setSize(Consts.DEFAULT_REQUEST_PARAM_SIZE);
	}
	
	public ListAlertFailRequest(String project, String alertName, int from, int to, int offset, int size) {
		super(project);
		setAlertName(alertName);
		setOffset(offset);
		setSize(size);
		setFrom(from);
		setTo(to);
	}

	public String getAlertName() {
		return GetParam(Consts.CONST_ALERT_NAME);
	}

	public void setAlertName(String alertName) {
		SetParam(Consts.CONST_ALERT_NAME, alertName);
	}

	public int getFrom() {
		return Integer.parseInt(GetParam(Consts.CONST_ALERT_FROM));
	}
	
	public void setFrom(int from) {
		SetParam(Consts.CONST_ALERT_FROM, String.valueOf(from));
	}
	
	public int getTo() {
		return Integer.parseInt(GetParam(Consts.CONST_ALERT_TO));
	}
	
	public void setTo(int to) {
		SetParam(Consts.CONST_ALERT_TO, String.valueOf(to));
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
