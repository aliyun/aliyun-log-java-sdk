package com.aliyun.openservices.log.request;

public class GetMetricStoreRequest extends Request {
  private static final long serialVersionUID = -8932189024638798559L;
  private String metricStore;

  public GetMetricStoreRequest(String project, String metricStore) {
    super(project);
    this.metricStore = metricStore;
  }

  /**
   * @return the metricStore
   */
  public String GetMetricStore() {
    return metricStore;
  }

  /**
   * @param metricStore the metricStore to set
   */
  public void SetMetricStore(String metricStore) {
    this.metricStore = metricStore;
  }
}
