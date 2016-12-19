package com.aliyun.openservices.log.request;

public class GetAlertRequest extends Request {

	private static final long serialVersionUID = 219859427246796382L;
	protected String alertName;

	public GetAlertRequest(String project, String alertName) {
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
