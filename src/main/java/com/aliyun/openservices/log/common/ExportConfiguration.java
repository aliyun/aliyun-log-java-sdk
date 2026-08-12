package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ExportConfiguration extends JobConfiguration {
    private String version;

    private String logstore;

    private String accessKeyId;

    private String accessKeySecret;

    private String roleArn;

    private String instanceType;

    private int fromTime;

    public int getToTime() {
        return toTime;
    }

    public void setToTime(int toTime) {
        this.toTime = toTime;
    }

    private int toTime;

    private DataSink sink;

    private Map<String, String> parameters;

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

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = super.toJsonObject();
        if (sink != null) {
            value.put("sink", sink.toJsonObject());
        }
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        logstore = value.getString("logstore");
        roleArn = value.getString("roleArn");
        accessKeyId = value.getString("accessKeyId");
        accessKeySecret = value.getString("accessKeySecret");
        instanceType = value.getString("instanceType");
        fromTime = value.getIntValue("fromTime");
        toTime = value.getIntValue("toTime");
        version = value.getString("version");
        JSONObject obj = value.getJSONObject("sink");
        // if version is exist, use ExportGeneralSink
        if (version != null && !version.isEmpty()) {
            sink = new ExportGeneralSink();
            sink.fromJsonObject(obj);
        } else {
            DataSinkType type = DataSinkType.fromString(obj.getString("type"));
            if (type == null) {
                throw new IllegalArgumentException("Unknown export sink type: " + obj.getString("type"));
            }
            switch (type) {
                case ALIYUN_ADB:
                    sink = new AliyunADBSink();
                    break;
                case ALIYUN_TSDB:
                    sink = new AliyunTSDBSink();
                    break;
                case ALIYUN_OSS:
                    sink = new AliyunOSSSink();
                    break;
                case ALIYUN_ODPS:
                    sink = new AliyunODPSSink();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported export sink type: " + type);
            }
            sink.fromJsonObject(obj);
        }
        parameters = JsonUtils.readOptionalMap(value, "parameters");
    }
}
