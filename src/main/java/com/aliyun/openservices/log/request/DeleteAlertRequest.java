package com.aliyun.openservices.log.request;

public class DeleteAlertRequest extends Request {

	private static final long serialVersionUID = -307796276524545979L;
	protected String alertName;

	public DeleteAlertRequest(String project, String alertName) {
		super(project);
		this.alertName = alertName;
	}

	public String getAlertName() {
		return alertName;
	}

	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}

}
