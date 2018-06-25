package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.Chart;

public class GetChartResponse extends Response {

	private static final long serialVersionUID = -5285312896064493977L;

	private Chart chart;

	public GetChartResponse(Map<String, String> headers, Chart chart) {
		super(headers);
		this.chart = chart;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

}
