package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Dashboard;

public class UpdateDashboardRequest extends Request {

	private static final long serialVersionUID = -7340328426947680281L;
	private Dashboard dashboard;

	public UpdateDashboardRequest(String project, Dashboard dashboard) {
		super(project);
		this.dashboard = dashboard;
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

}
