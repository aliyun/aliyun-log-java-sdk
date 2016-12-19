package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListAlertResponse extends Response {

	private static final long serialVersionUID = 167254009014902401L;
	protected int total  = 0;
	protected int count = 0;
	protected List<String> alerts = new ArrayList<String>();
	
	public ListAlertResponse(Map<String, String> headers, int count, int total, List<String> alerts) {
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

	public List<String> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<String> alerts) {
		this.alerts = new ArrayList<String>();
		for (String alert : alerts) {
			alerts.add(alert);
		}
	}

}
