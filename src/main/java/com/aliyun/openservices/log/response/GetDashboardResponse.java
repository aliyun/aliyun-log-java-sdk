package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Dashboard;

public class GetDashboardResponse extends Response {

	private static final long serialVersionUID = -2280928012073397968L;

	private Dashboard dashboard;

	public GetDashboardResponse(Map<String, String> headers, Dashboard dashboard) {
		super(headers);
		this.dashboard = dashboard;	
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

}
