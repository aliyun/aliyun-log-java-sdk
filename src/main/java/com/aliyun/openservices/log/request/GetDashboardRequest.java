package com.aliyun.openservices.log.request;

public class GetDashboardRequest extends Request {

	private static final long serialVersionUID = -4151589357662041225L;
	private String dashboardName = "";

	public GetDashboardRequest(String project, String dashboardName) {
		super(project);
		this.dashboardName = dashboardName;
	}

	public String getDashboardName() {
		return dashboardName;
	}

	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}

}
