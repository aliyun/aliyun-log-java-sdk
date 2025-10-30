package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MetricStore;

public class CreateMetricStoreRequest extends Request {
    private static final long serialVersionUID = 7955483608406998451L;
	protected MetricStore metricStore = new MetricStore();

	public CreateMetricStoreRequest(String project, MetricStore metricStore) {
		super(project);
		setMetricStore(metricStore);
	}
	
	public MetricStore getMetricStore() {
		return metricStore;
	}

	public void setMetricStore(MetricStore metricStore) {
		this.metricStore = new MetricStore(metricStore);
	}
}
