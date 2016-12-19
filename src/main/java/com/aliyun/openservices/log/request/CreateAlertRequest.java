package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Alert;

public class CreateAlertRequest extends Request {

	private static final long serialVersionUID = -810657184784383867L;
	protected Alert alert = new Alert();

	public CreateAlertRequest(String project, Alert alert) {
		super(project);
		this.alert = alert;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}

}
