package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Alert;
import com.aliyun.openservices.log.exception.LogException;

public class GetAlertResponse extends Response {

	private static final long serialVersionUID = -5912788622244096859L;
	protected Alert alert = new Alert(); 

	public GetAlertResponse(Map<String, String> headers, Alert alert) {
		super(headers);
		this.alert = alert;
	}

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) throws LogException {
		this.alert = new Alert(alert);
	}

}
