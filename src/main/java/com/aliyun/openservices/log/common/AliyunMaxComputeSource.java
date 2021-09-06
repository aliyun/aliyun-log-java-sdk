package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

public class AliyunMaxComputeSource extends DataSource {
    private String accessKeyID;
    private String accessKeySecret;
    private String endpoint;
    private String tunnelEndpoint;
    private String project;
    private String table;
    private String partitionSpec;
    private String timeField;
    private String timeFormat;
    private String timeZone;

    public AliyunMaxComputeSource() {
        super(DataSourceType.ALIYUN_MAX_COMPUTE);
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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTunnelEndpoint() {
        return tunnelEndpoint;
    }

    public void setTunnelEndpoint(String tunnelEndpoint) {
        this.tunnelEndpoint = tunnelEndpoint;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPartitionSpec() {
        return partitionSpec;
    }

    public void setPartitionSpec(String partitionSpec) {
        this.partitionSpec = partitionSpec;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        super.deserialize(jsonObject);
        accessKeyID = JsonUtils.readOptionalString(jsonObject, "accessKeyID");
        accessKeySecret = JsonUtils.readOptionalString(jsonObject, "accessKeySecret");
        endpoint = JsonUtils.readOptionalString(jsonObject, "endpoint");
        tunnelEndpoint = JsonUtils.readOptionalString(jsonObject, "tunnelEndpoint");
        project = JsonUtils.readOptionalString(jsonObject, "project");
        table = JsonUtils.readOptionalString(jsonObject, "table");
        partitionSpec = JsonUtils.readOptionalString(jsonObject, "partitionSpec");
        timeField = JsonUtils.readOptionalString(jsonObject, "timeField");
        timeFormat = JsonUtils.readOptionalString(jsonObject, "timeFormat");
        timeZone = JsonUtils.readOptionalString(jsonObject, "timeZone");
    }
}
