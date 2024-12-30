package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class Parameter implements Serializable {
    //rds-vpc
    @JSONField(name = "vpc-id")
    private String vpcId;
    @JSONField(name = "instance-id")
    private String instanceId;
    private String host;
    private String port;
    private String table;
    private String username;

    private String password;
    @JSONField(name = "db")
    private String database;

    private String region;
    //oss
    private String accessid;
    private String accesskey;
    private String endpoint;
    private String bucket;

    // csv
    private String externalStoreCsv;
    private int externalStoreCsvSize;
    private List<String> objects;
    private List<CsvColumn> columns;

    public Parameter(String vpcId, String instanceId, String host, String port, String username, String password, String database, String table, String region, String accessid, String accesskey, String endpoint, String bucket) {
        this.vpcId = vpcId;
        this.instanceId = instanceId;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.table = table;
        this.region = region;
        this.accessid = accessid;
        this.accesskey = accesskey;
        this.endpoint = endpoint;
        this.bucket = bucket;
    }

    // csv
    public Parameter(String externalStoreCsv, int externalStoreCsvSize, List<String> objects, List<CsvColumn> columns) {
        this.externalStoreCsv = externalStoreCsv;
        this.externalStoreCsvSize = externalStoreCsvSize;
        this.objects = objects;
        this.columns = columns;
    }

    public Parameter() {
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getAccessid() {
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getExternalStoreCsv() {
        return externalStoreCsv;
    }

    public void setExternalStoreCsv(String externalStoreCsv) {
        this.externalStoreCsv = externalStoreCsv;
    }

    public int getExternalStoreCsvSize() {
        return externalStoreCsvSize;
    }

    public void setExternalStoreCsvSize(int externalStoreCsvSize) {
        this.externalStoreCsvSize = externalStoreCsvSize;
    }

    public List<String> getObjects() {
        return objects;
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }

    public List<CsvColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<CsvColumn> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "vpcId='" + vpcId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", region='" + region + '\'' +
                ", bucket='" + bucket + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", externalStoreCsvSize=" + externalStoreCsvSize +
                ", objects=" + objects +
                ", columns=" + columns +
                '}';
    }
}
