package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListDashboardResponse extends Response {

	private static final long serialVersionUID = 3511511842342055252L;
	protected int count = 0;
	protected int total = 0;
	protected List<String> dashboards = new ArrayList<String>();

	public ListDashboardResponse(Map<String, String> headers, int count, int total, List<String> dashboards) {
		super(headers);
		setCount(count);
		setTotal(total);
		this.dashboards = dashboards;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<String> getDashboards() {
		return dashboards;
	}

	public void setDashboards(List<String> dashboards) {
		this.dashboards = new ArrayList<String>();
		for (String dashboard : dashboards) {
			this.dashboards.add(dashboard);
		}
	}
}
