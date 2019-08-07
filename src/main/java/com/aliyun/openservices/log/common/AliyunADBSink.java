package com.aliyun.openservices.log.common;


import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class AliyunADBSink extends DataSink {

    private String url;

    private String user;

    private String password;

    private String dbType;

    private String regionId;

    private String zoneId;

    private String database;

    private String tableGroupName;

    private String table;

    private int batchSize;

    private boolean strictMode;

    private HashMap<String, String> columnMapping;

    public AliyunADBSink() {
        super(DataSinkType.ALIYUN_ADB);
    }

    public AliyunADBSink(DataSinkType type, String url, String user, String password, String dbType, String regionId, String zoneId, String database, String tableGroupName, String table, int batchSize, boolean strictMode, HashMap<String, String> columnMapping) {
        super(type);
        this.url = url;
        this.user = user;
        this.password = password;
        this.dbType = dbType;
        this.regionId = regionId;
        this.zoneId = zoneId;
        this.database = database;
        this.tableGroupName = tableGroupName;
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

    @Override
    public void deserialize(JSONObject value) {
        url = value.getString("url");
        user = value.getString("user");
        password = value.getString("password");
        dbType = value.getString("dbType");
        regionId = value.getString("regionId");
        zoneId = value.getString("zoneId");
        database = value.getString("database");
        tableGroupName = value.getString("tableGroupName");
        table = value.getString("table");
        batchSize = value.getInt("batchSize");
        strictMode = value.getBoolean("strictMode");
        JSONObject cm = value.getJSONObject("columnMapping");
        Iterator iterator = cm.keys();
        columnMapping = new HashMap<String, String>();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            columnMapping.put(key, cm.getString(key));
        }
    }
}
