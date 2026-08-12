package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.annotation.InternalApi;
import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AliyunODPSSink extends DataSink {
    private String odpsRolearn;
    private String odpsEndpoint;
    private String odpsTunnelEndpoint;
    private String odpsProject;
    private String odpsTable;
    private String timeZone;
    private String partitionTimeFormat;
    private List<String> fields;
    private List<String> partitionColumn;
    private String odpsAccessKeyId;
    private String odpsAccessSecret;
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getOdpsAccessKeyId() {
        return odpsAccessKeyId;
    }

    public void setOdpsAccessKeyId(String odpsAccessKeyId) {
        this.odpsAccessKeyId = odpsAccessKeyId;
    }

    public String getOdpsAccessSecret() {
        return odpsAccessSecret;
    }

    public void setOdpsAccessSecret(String odpsAccessSecret) {
        this.odpsAccessSecret = odpsAccessSecret;
    }

    public String getOdpsRolearn() {
        return odpsRolearn;
    }

    public void setOdpsRolearn(String odpsRolearn) {
        this.odpsRolearn = odpsRolearn;
    }

    public String getOdpsEndpoint() {
        return odpsEndpoint;
    }

    public void setOdpsEndpoint(String odpsEndpoint) {
        this.odpsEndpoint = odpsEndpoint;
    }

    public String getOdpsTunnelEndpoint() {
        return odpsTunnelEndpoint;
    }

    public void setOdpsTunnelEndpoint(String odpsTunnelEndpoint) {
        this.odpsTunnelEndpoint = odpsTunnelEndpoint;
    }

    public String getOdpsProject() {
        return odpsProject;
    }

    public void setOdpsProject(String odpsProject) {
        this.odpsProject = odpsProject;
    }

    public String getOdpsTable() {
        return odpsTable;
    }

    public void setOdpsTable(String odpsTable) {
        this.odpsTable = odpsTable;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getPartitionTimeFormat() {
        return partitionTimeFormat;
    }

    public void setPartitionTimeFormat(String partitionTimeFormat) {
        this.partitionTimeFormat = partitionTimeFormat;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(String... fields) {
        this.fields = Arrays.asList(fields);
    }

    public List<String> getPartitionColumn() {
        return partitionColumn;
    }

    public void setPartitionColumn(String... partitionColumn) {
        this.partitionColumn = Arrays.asList(partitionColumn);
    }
    public AliyunODPSSink() {
        super(DataSinkType.ALIYUN_ODPS);
        partitionColumn = new ArrayList<String>();
        fields = new ArrayList<String>();
    }

    @Override
    @InternalApi
    public JSONObject toJsonObject() {
        JSONObject value = new JSONObject();
        value.put("type", getType().toString());
        put(value, "odpsRolearn", odpsRolearn);
        put(value, "odpsEndpoint", odpsEndpoint);
        put(value, "odpsTunnelEndpoint", odpsTunnelEndpoint);
        put(value, "odpsProject", odpsProject);
        put(value, "odpsTable", odpsTable);
        put(value, "timeZone", timeZone);
        put(value, "partitionTimeFormat", partitionTimeFormat);
        if (fields != null) {
            value.put("fields", JsonCodec.toJsonArray(fields));
        }
        if (partitionColumn != null) {
            value.put("partitionColumn", JsonCodec.toJsonArray(partitionColumn));
        }
        put(value, "odpsAccessKeyId", odpsAccessKeyId);
        put(value, "odpsAccessSecret", odpsAccessSecret);
        put(value, "mode", mode);
        return value;
    }

    @Override
    @InternalApi
    public void fromJsonObject(JSONObject value) {
        odpsRolearn = value.getString("odpsRolearn");
        odpsEndpoint = value.getString("odpsEndpoint");
        odpsTunnelEndpoint = value.getString("odpsTunnelEndpoint");
        odpsProject = value.getString("odpsProject");
        odpsTable = value.getString("odpsTable");
        timeZone = value.getString("timeZone");
        partitionTimeFormat = value.getString("partitionTimeFormat");
        fields = JsonUtils.readStringList(value, "fields");
        partitionColumn = JsonUtils.readStringList(value, "partitionColumn");
        odpsAccessKeyId = value.getString("odpsAccessKeyId");
        odpsAccessSecret = value.getString("odpsAccessSecret");
        mode = value.getString("mode");
    }

    private static void put(JSONObject value, String key, String item) {
        if (item != null) {
            value.put(key, item);
        }
    }
}
