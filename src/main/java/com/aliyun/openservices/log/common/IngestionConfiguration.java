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

    @Override
    public void deserialize(JSONObject value) {
        logstore = value.getString("logstore");
        JSONObject jsonObject = value.getJSONObject("source");
        DataSourceType kind = DataSourceType.fromString(jsonObject.getString("type"));
        if (kind == DataSourceType.ALIYUN_OSS) {
            source = new AliyunOSSSource();
            source.deserialize(jsonObject);
        }
    }
}
