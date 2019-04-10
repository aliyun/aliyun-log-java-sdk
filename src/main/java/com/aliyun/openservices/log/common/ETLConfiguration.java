package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.openservices.log.util.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ETLConfiguration extends JobConfiguration {

    @JSONField
    private String script;

    @JSONField
    private String logstore;

    @JSONField
    private String instanceType;

    @JSONField
    private String containerImage;

    @JSONField
    private int version;

    @JSONField
    private List<DataSink> sinks;

    @JSONField
    private Map<String, String> parameters;

    @JSONField
    private String accessKeyId;

    @JSONField
    private String accessKeySecret;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getContainerImage() {
        return containerImage;
    }

    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<DataSink> getSinks() {
        return sinks;
    }

    public void setSinks(List<DataSink> sinks) {
        this.sinks = sinks;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
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

    @Override
    public void deserialize(JSONObject value) {
        script = value.getString("script");
        logstore = value.getString("logstore");
        version = value.getInt("version");
        instanceType = JsonUtils.readOptionalString(value, "instanceType");
        containerImage = JsonUtils.readOptionalString(value, "containerImage");
        parameters = JsonUtils.readOptionalMap(value, "parameters");
        JSONArray sinks = value.getJSONArray("sinks");
        this.sinks = new ArrayList<DataSink>(sinks.size());
        for (int i = 0; i < sinks.size(); i++) {
            DataSink sink = new DataSink();
            sink.deserialize(sinks.getJSONObject(i));
            this.sinks.add(sink);
        }
        accessKeyId = value.getString("accessKeyId");
        accessKeySecret = value.getString("accessKeySecret");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ETLConfiguration that = (ETLConfiguration) o;

        if (getVersion() != that.getVersion()) return false;
        if (getScript() != null ? !getScript().equals(that.getScript()) : that.getScript() != null) return false;
        if (getLogstore() != null ? !getLogstore().equals(that.getLogstore()) : that.getLogstore() != null)
            return false;
        if (getInstanceType() != null ? !getInstanceType().equals(that.getInstanceType()) : that.getInstanceType() != null)
            return false;
        if (getContainerImage() != null ? !getContainerImage().equals(that.getContainerImage()) : that.getContainerImage() != null)
            return false;
        if (getSinks() != null ? !getSinks().equals(that.getSinks()) : that.getSinks() != null) return false;
        if (getParameters() != null ? !getParameters().equals(that.getParameters()) : that.getParameters() != null)
            return false;
        if (getAccessKeyId() != null ? !getAccessKeyId().equals(that.getAccessKeyId()) : that.getAccessKeyId() != null)
            return false;
        return getAccessKeySecret() != null ? getAccessKeySecret().equals(that.getAccessKeySecret()) : that.getAccessKeySecret() == null;
    }

    @Override
    public int hashCode() {
        int result = getScript() != null ? getScript().hashCode() : 0;
        result = 31 * result + (getLogstore() != null ? getLogstore().hashCode() : 0);
        result = 31 * result + (getInstanceType() != null ? getInstanceType().hashCode() : 0);
        result = 31 * result + (getContainerImage() != null ? getContainerImage().hashCode() : 0);
        result = 31 * result + getVersion();
        result = 31 * result + (getSinks() != null ? getSinks().hashCode() : 0);
        result = 31 * result + (getParameters() != null ? getParameters().hashCode() : 0);
        result = 31 * result + (getAccessKeyId() != null ? getAccessKeyId().hashCode() : 0);
        result = 31 * result + (getAccessKeySecret() != null ? getAccessKeySecret().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ETLConfiguration{" +
                "script='" + script + '\'' +
                ", logstore='" + logstore + '\'' +
                ", instanceType='" + instanceType + '\'' +
                ", containerImage='" + containerImage + '\'' +
                ", version=" + version +
                ", sinks=" + sinks +
                ", parameters=" + parameters +
                ", accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                '}';
    }
}
