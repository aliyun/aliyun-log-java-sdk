package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Chart;

public class GetChartRequest extends Request {

	private static final long serialVersionUID = 898227016586837966L;
	String dashboardName = "";
	String chartName = "";

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

	public GetChartRequest(String project, String dashboardName, String chartName) {
		super(project);
		this.chartName = chartName;
		this.dashboardName = dashboardName;
	}

}
