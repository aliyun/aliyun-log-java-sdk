package com.aliyun.openservices.log.common;

import net.sf.json.JSONObject;

public class ExportConfiguration extends JobConfiguration {

    private String logstore;

    private String accessKeyId;

    private String accessKeySecret;

    private String instanceType;

    private int fromTime;

    private DataSink sink;

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public int getFromTime() {
        return fromTime;
    }

    public void setFromTime(int fromTime) {
        this.fromTime = fromTime;
    }

    public DataSink getSink() {
        return sink;
    }

    public void setSink(DataSink sink) {
        this.sink = sink;
    }

    @Override
    public void deserialize(JSONObject value) {
        logstore = value.getString("logstore");
        accessKeyId = value.getString("accessKeyId");
        accessKeySecret = value.getString("accessKeySecret");
        instanceType = value.getString("instanceType");
        fromTime = value.getInt("fromTime");
        JSONObject obj = value.getJSONObject("sink");
        DataSinkType type = DataSinkType.fromString(obj.getString("type"));
        if (type == DataSinkType.ALIYUN_ADB) {
            sink = new AliyunADBSink();
            sink.deserialize(obj);
        } else if (type == DataSinkType.ALIYUN_TSDB) {
            sink = new AliyunTSDBSink();
            sink.deserialize(obj);
        }
    }

}
