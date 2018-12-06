package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Dashboard;

public class ListDashboardResponse extends Response {

	private static final long serialVersionUID = 3511511842342055252L;
	protected int count = 0;
	protected int total = 0;
	protected List<Dashboard> dashboards = new ArrayList<Dashboard>();

	public ListDashboardResponse(Map<String, String> headers, int count, int total, List<Dashboard> dashboards) {
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

	public List<Dashboard> getDashboards() {
		return dashboards;
	}

	public void setDashboards(List<Dashboard> dashboards) {
		this.dashboards = new ArrayList<Dashboard>();
		for (Dashboard dashboard : dashboards) {
			this.dashboards.add(dashboard);
		}
	}
}
