package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Alert;

public class ListAlertResponse extends Response {

	private static final long serialVersionUID = 167254009014902401L;
	protected int total  = 0;
	protected int count = 0;
	protected List<Alert> alerts = new ArrayList<Alert>();
	
	public ListAlertResponse(Map<String, String> headers, int count, int total, List<Alert> alerts) {
		super(headers);
		setCount(count);
		setTotal(total);
		this.alerts = alerts;
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

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = new ArrayList<Alert>();
		for (Alert alert : alerts) {
			alerts.add(alert);
		}
	}

}
