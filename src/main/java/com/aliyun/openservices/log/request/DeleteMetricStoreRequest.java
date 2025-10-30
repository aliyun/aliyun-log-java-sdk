package com.aliyun.openservices.log.request;

public class DeleteMetricStoreRequest extends Request {
  private static final long serialVersionUID = 2597932397395479932L;
  protected String metricStoreName;

  public DeleteMetricStoreRequest(String project, String metricStoreName) {
    super(project);
    this.metricStoreName = metricStoreName;
  }

  public String GetMetricStoreName() {
    return metricStoreName;
  }

  public void SetMetricStoreName(String metricStoreName) {
    this.metricStoreName = metricStoreName;
  }
}
