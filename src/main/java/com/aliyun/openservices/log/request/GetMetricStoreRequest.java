package com.aliyun.openservices.log.request;

public class GetMetricStoreRequest extends Request {
  private static final long serialVersionUID = -8932189024638798559L;
  private String metricStore;

  public GetMetricStoreRequest(String project, String metricStore) {
    super(project);
    this.metricStore = metricStore;
  }

  /**
   * @return the metricStore name
   */
  public String getMetricStoreName() {
    return metricStore;
  }

  /**
   * @param metricStore the metricStore name to set
   */
  public void setMetricStoreName(String metricStore) {
    this.metricStore = metricStore;
  }
}
