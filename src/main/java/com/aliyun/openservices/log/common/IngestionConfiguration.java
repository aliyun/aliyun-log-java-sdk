package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

public class IngestionConfiguration extends JobConfiguration {

    private String logstore;

    private DataSource source;

    private Integer numberOfInstances;

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public DataSource getSource() {
        return source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public Integer getNumberOfInstances() {
        return numberOfInstances;
    }

    public void setNumberOfInstances(Integer numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }

    private DataSource createSource(DataSourceType type) {
        switch (type) {
            case JDBC:
                return new JDBCSource();
            case ALIYUN_BSS:
                return new AliyunBSSSource();
            case ALIYUN_OSS:
                return new AliyunOSSSource();
            case ALIYUN_MAX_COMPUTE:
                return new AliyunMaxComputeSource();
            case KAFKA:
                return new KafKaSource();
            case ALIYUN_CLOUD_MONITOR:
                return new AliyunCloudMonitorSource();
            default:
                return null;
        }
    }

    @Override
    public void deserialize(JSONObject value) {
        logstore = value.getString("logstore");
        numberOfInstances = value.getIntValue("numberOfInstances");
        JSONObject jsonObject = value.getJSONObject("source");
        DataSourceType kind = DataSourceType.fromString(jsonObject.getString("type"));
        source = createSource(kind);
        if (source != null) {
            source.deserialize(jsonObject);
        }
    }
}
