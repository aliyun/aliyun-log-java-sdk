package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Chart;

public class CreateChartRequest extends Request{
	
	private static final long serialVersionUID = -8733254344842470177L;
	private String dashboardName = "";
	private Chart chart = new Chart();

	public CreateChartRequest(String project, String dashboardName, Chart chart) {
		super(project);
		this.dashboardName = dashboardName;
		this.chart = chart;
	}

	public String getDashboardName() {
		return dashboardName;
	}

	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

}
