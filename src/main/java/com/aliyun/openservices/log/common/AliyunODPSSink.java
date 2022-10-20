package com.aliyun.openservices.log.common;

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
}
