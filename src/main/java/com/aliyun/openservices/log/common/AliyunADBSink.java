package com.aliyun.openservices.log.common;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.util.JsonUtils;

import java.util.HashMap;
import java.util.Set;

public class AliyunADBSink extends DataSink {

    private static final String ADB_V2 = "adb20";

    private static final String ADB_V3 = "adb30";

    private String url;

    private String user;

    private String password;

    private String dbType;

    //required for v2
    private String regionId = "";

    //required for v2
    private String zoneId = "";

    //required for v2
    private String tableGroupName = "";

    //optional for v2, required for v3
    private String vpcId = "";

    //optional for v2, required for v3
    private String instanceId = "";

    //optional for v2, required for v3
    private String instancePort = "";

    private String database;

    private String table;

    private int batchSize;

    private boolean strictMode;

    private HashMap<String, String> columnMapping;

    public AliyunADBSink() {
        super(DataSinkType.ALIYUN_ADB);
    }

    public void BuildAliyunADBV2Sink(String url, String user, String password, String regionId, String zoneId, String database, String tableGroupName, String table, int batchSize, boolean strictMode, HashMap<String, String> columnMapping) {
        this.dbType = ADB_V2;
        this.url = url;
        this.user = user;
        this.password = password;
        this.regionId = regionId;
        this.zoneId = zoneId;
        this.database = database;
        this.tableGroupName = tableGroupName;
        this.table = table;
        this.batchSize = batchSize;
        this.strictMode = strictMode;
        this.columnMapping = columnMapping;
    }

    public void BuildAliyunADBV3Sink(String url, String user, String password, String vpcId, String instanceId, String instancePort, String database, String table, int batchSize, boolean strictMode, HashMap<String, String> columnMapping) {
        this.dbType = ADB_V3;
        this.url = url;
        this.user = user;
        this.password = password;
        this.vpcId = vpcId;
        this.instanceId = instanceId;
        this.instancePort = instancePort;
        this.database = database;
        this.table = table;
        this.batchSize = batchSize;
        this.strictMode = strictMode;
        this.columnMapping = columnMapping;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
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

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public HashMap<String, String> getColumnMapping() {
        return columnMapping;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getTableGroupName() {
        return tableGroupName;
    }

    public void setTableGroupName(String tableGroupName) {
        this.tableGroupName = tableGroupName;
    }

    public void setColumnMapping(HashMap<String, String> columnMapping) {
        this.columnMapping = columnMapping;
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

    public String getInstancePort() {
        return instancePort;
    }

    public void setInstancePort(String instancePort) {
        this.instancePort = instancePort;
    }

    @Override
    public void deserialize(JSONObject value) {
        url = value.getString("url");
        user = value.getString("user");
        password = value.getString("password");
        dbType = value.getString("dbType");
        if (dbType.equals(ADB_V2)) {
            regionId = value.getString("regionId");
            zoneId = value.getString("zoneId");
            tableGroupName = value.getString("tableGroupName");
            vpcId = JsonUtils.readOptionalString(value, "vpcId", "");
            instanceId = JsonUtils.readOptionalString(value, "instanceId", "");
            instancePort = JsonUtils.readOptionalString(value, "instancePort", "");
        } else {
            regionId = JsonUtils.readOptionalString(value, "regionId", "");
            zoneId = JsonUtils.readOptionalString(value, "zoneId", "");
            tableGroupName = JsonUtils.readOptionalString(value, "tableGroupName", "");
            vpcId = value.getString("vpcId");
            instanceId = value.getString("instanceId");
            instancePort = value.getString("instancePort");
        }
        database = value.getString("database");
        table = value.getString("table");
        batchSize = value.getIntValue("batchSize");
        strictMode = value.getBoolean("strictMode");
        JSONObject cm = value.getJSONObject("columnMapping");
        Set<String> keySet = cm.keySet();
        columnMapping = new HashMap<String, String>();
        for (String key : keySet) {
            columnMapping.put(key, cm.getString(key));
        }
    }

}
