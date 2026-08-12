package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.annotation.InternalApi;

public class IngestionConfiguration extends JobConfiguration {
    private String version;

    private String logstore;

    private DataSource source;

    private Integer numberOfInstances;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

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

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (source != null) {
            value.put("source", source.toJsonObject());
        }
        return value;
    }

    private DataSource createSource(JSONObject jsonObject) {
        if (version != null && !version.isEmpty()) {
            return new IngestionGeneralSource();
        }
        DataSourceType type = DataSourceType.fromString(jsonObject.getString("type"));
        if (type == null) {
            throw new IllegalArgumentException("Unknown ingestion source type: " + jsonObject.getString("type"));
        }
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
                throw new IllegalArgumentException("Unsupported ingestion source type: " + type);
        }
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        version = value.getString("version");
        logstore = value.getString("logstore");
        numberOfInstances = value.getIntValue("numberOfInstances");
        JSONObject jsonObject = value.getJSONObject("source");
        source = createSource(jsonObject);
        source.fromJsonObject(jsonObject);
    }
}
