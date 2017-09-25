package com.aliyun.openservices.log.request;

public class DeleteChartRequest extends Request {

	private static final long serialVersionUID = 6209258077667198663L;
	private String dashboardName = "";
	private String chartName = "";

	public DeleteChartRequest(String project, String dashboardName, String chartName) {
		super(project);
		this.dashboardName = dashboardName;
		this.chartName = chartName;
	}

	public String getDashboardName() {
		return dashboardName;
	}

	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

}
