package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Dashboard;

public class CreateDashboardRequest extends Request {

	private static final long serialVersionUID = 7004536467145235084L;
	private Dashboard dashboard = new Dashboard();

	public CreateDashboardRequest(String project, Dashboard dashboard) {
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
