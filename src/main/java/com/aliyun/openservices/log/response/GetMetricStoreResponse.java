package com.aliyun.openservices.log.response;

import java.util.Map;

import com.aliyun.openservices.log.common.MetricStore;

public class GetMetricStoreResponse extends Response {
  private static final long serialVersionUID = -4265633972358905206L;

  MetricStore metricStore = new MetricStore();

  public GetMetricStoreResponse(Map<String, String> headers, MetricStore metricStore) {
    super(headers);
    SetMetricStore(metricStore);
  }

  public MetricStore GetMetricStore() {
    return metricStore;
  }

  public void SetMetricStore(MetricStore metricStore) {
    this.metricStore = new MetricStore(metricStore);
  }
}
