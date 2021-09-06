package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.util.List;

public class AliyunCloudMonitorSource extends DataSource {
    private String accessKeyID;
    private String accessKeySecret;
    private long startTime;
    private List<String> namespaces;
    private String outputType;
    private Integer delayTime;

    public AliyunCloudMonitorSource() {
        super(DataSourceType.ALIYUN_CLOUD_MONITOR);
    }

    public String getAccessKeyID() {
        return accessKeyID;
    }

    public void setAccessKeyID(String accessKeyID) {
        this.accessKeyID = accessKeyID;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Integer getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        accessKeyID = jsonObject.getString("accessKeyID");
        accessKeySecret = jsonObject.getString("accessKeySecret");
        startTime = jsonObject.getLongValue("startTime");
        namespaces = JsonUtils.readOptionalStrings(jsonObject, "namespaces");
        outputType = jsonObject.getString("outputType");
        delayTime = jsonObject.getInteger("delayTime");
    }
}
