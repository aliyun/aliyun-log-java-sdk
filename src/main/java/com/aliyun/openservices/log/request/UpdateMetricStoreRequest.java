package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.MetricStore;

public class UpdateMetricStoreRequest extends Request {
	private static final long serialVersionUID = -2555582962386526246L;
	protected MetricStore metricStore = new MetricStore();

	public UpdateMetricStoreRequest(String project, MetricStore metricStore) {
		super(project);
		SetMetricStore(metricStore);
	}
	
	public MetricStore GetMetricStore() {
		return metricStore;
	}

	public void SetMetricStore(MetricStore metricStore) {
		this.metricStore = new MetricStore(metricStore);
	}
}

