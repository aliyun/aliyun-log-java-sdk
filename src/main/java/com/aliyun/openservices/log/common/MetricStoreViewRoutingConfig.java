package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class MetricStoreViewRoutingConfig {

    @JSONField(name = "metric_names")
    private List<String> metricNames;

    @JSONField(name = "project_stores")
    private List<ProjectStore> projectStores;


    public static class ProjectStore {

        @JSONField(name = "project")
        private String projectName;

        @JSONField(name = "metricstore")
        private String metricStore;

        public ProjectStore(String projectName, String metricStore) {
            this.projectName = projectName;
            this.metricStore = metricStore;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getMetricStore() {
            return metricStore;
        }

        public void setMetricStore(String metricStore) {
            this.metricStore = metricStore;
        }
    }

    public MetricStoreViewRoutingConfig(List<String> metricNames, List<ProjectStore> projectStores) {
        this.metricNames = metricNames;
        this.projectStores = projectStores;
    }

    public List<String> getMetricNames() {
        return metricNames;
    }

    public void setMetricNames(List<String> metricNames) {
        this.metricNames = metricNames;
    }

    public List<ProjectStore> getProjectStores() {
        return projectStores;
    }

    public void setProjectStores(List<ProjectStore> projectStores) {
        this.projectStores = projectStores;
    }
}
