package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

public class IngestionConfiguration extends JobConfiguration {

    private String logstore;

    private DataSource source;

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

    private DataSource createSource(DataSourceType type) {
        switch (type) {
            case JDBC:
                return new JDBCSource();
            case ALIYUN_BSS:
                return new AliyunBSSSource();
            case ALIYUN_OSS:
                return new AliyunOSSSource();
            default:
                return null;
        }
    }

    @Override
    public void deserialize(JSONObject value) {
        logstore = value.getString("logstore");
        JSONObject jsonObject = value.getJSONObject("source");
        DataSourceType kind = DataSourceType.fromString(jsonObject.getString("type"));
        source = createSource(kind);
        if (source != null) {
            source.deserialize(jsonObject);
        }
    }
}
