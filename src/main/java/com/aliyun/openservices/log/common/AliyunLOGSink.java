package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class AliyunLOGSink extends DataSink {

    @JSONField
    private String name;

    @JSONField
    private String project;

    @JSONField
    private String logstore;

    @JSONField
    private String accessKeyId;

    @JSONField
    private String accessKeySecret;

    public AliyunLOGSink() {
        super(DataSinkType.ALIYUN_LOG);
    }

    public AliyunLOGSink(String name, String project, String logstore) {
        super(DataSinkType.ALIYUN_LOG);
        this.name = name;
        this.project = project;
        this.logstore = logstore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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

    public void deserialize(JSONObject value) {
        name = value.getString("name");
        project = value.getString("project");
        logstore = value.getString("logstore");
        accessKeyId = value.getString("accessKeyId");
        accessKeySecret = value.getString("accessKeySecret");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AliyunLOGSink sink = (AliyunLOGSink) o;

        if (getName() != null ? !getName().equals(sink.getName()) : sink.getName() != null) return false;
        if (getProject() != null ? !getProject().equals(sink.getProject()) : sink.getProject() != null) return false;
        if (getLogstore() != null ? !getLogstore().equals(sink.getLogstore()) : sink.getLogstore() != null)
            return false;
        if (getAccessKeyId() != null ? !getAccessKeyId().equals(sink.getAccessKeyId()) : sink.getAccessKeyId() != null)
            return false;
        return getAccessKeySecret() != null ? getAccessKeySecret().equals(sink.getAccessKeySecret()) : sink.getAccessKeySecret() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getProject() != null ? getProject().hashCode() : 0);
        result = 31 * result + (getLogstore() != null ? getLogstore().hashCode() : 0);
        result = 31 * result + (getAccessKeyId() != null ? getAccessKeyId().hashCode() : 0);
        result = 31 * result + (getAccessKeySecret() != null ? getAccessKeySecret().hashCode() : 0);
        return result;
    }
}
