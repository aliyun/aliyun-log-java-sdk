package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Alert;

public class UpdateAlertRequest extends Request {

	private static final long serialVersionUID = -6487921750926014144L;
	protected Alert alert = new Alert();

	public UpdateAlertRequest(String project, Alert alert) {
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
