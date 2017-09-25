package com.aliyun.openservices.log.request;

public class DeleteDashboardRequest extends Request {

	private static final long serialVersionUID = -3646891562164325244L;
	private String dashboardName = "";

	public DeleteDashboardRequest(String project, String dashboardName) {
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
