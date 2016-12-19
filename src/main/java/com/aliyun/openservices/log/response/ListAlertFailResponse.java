package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.AlertFail;

public class ListAlertFailResponse extends Response {

	private static final long serialVersionUID = -4123941444921096160L;
	protected int total  = 0;
	protected int count = 0;
	protected List<AlertFail> alertFails = new ArrayList<AlertFail>();
	
	public ListAlertFailResponse(Map<String, String> headers, int count, int total, List<AlertFail> alertFails) {
		super(headers);
		setCount(count);
		setTotal(total);
		setAlertFails(alertFails);
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<AlertFail> getAlertFails() {
		return alertFails;
	}

	public void setAlertFails(List<AlertFail> alertFails) {
		this.alertFails = new ArrayList<AlertFail>();
		for (AlertFail alertFail:alertFails) {
			this.alertFails.add(alertFail);
		}
	}
}
