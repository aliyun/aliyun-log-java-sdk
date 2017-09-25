package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.Chart;

public class UpdateChartRequest extends Request {

	private static final long serialVersionUID = 3640423461560603319L;
	private String dashboardName = "";
	private String chartName = "";
	private Chart chart = new Chart();

	public UpdateChartRequest(String project, String dashboardName, String chartName, Chart chart) {
		super(project);
		this.dashboardName = dashboardName;
		this.chartName = chartName;
		this.chart = chart;
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

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

}
