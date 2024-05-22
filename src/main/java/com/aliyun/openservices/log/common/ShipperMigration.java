package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShipperMigration implements Serializable {
    private String name = "";
    private String logstore = "";
    private String region = "";
    private String shipperName = "";
    private String shipperType = "";
    private String exportLink = "";
    private String state = "";
    private int retryCount = 0;
    private String message = "";
    private String userConf = "";
    private long createTime = 0;
    private long updateTime = 0;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getShipperType() {
        return shipperType;
    }

    public void setShipperType(String shipperType) {
        this.shipperType = shipperType;
    }

    public String getExportLink() {
        return exportLink;
    }

    public void setExportLink(String exportLink) {
        this.exportLink = exportLink;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserConf() {
        return userConf;
    }

    public void setUserConf(String userConf) {
        this.userConf = userConf;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public ShipperMigration(String name, String logstore, String shipperName) {
        this.name = name;
        this.logstore = logstore;
        this.shipperName = shipperName;
    }

    public void  checkForCreate() throws IllegalArgumentException {
        checkStringEmpty("name", name);
        checkStringEmpty("logstore", logstore);
        checkStringEmpty("shipperName", shipperName);
    }

    public String ToCreateJsonString() throws LogException {
        JSONObject result = new JSONObject();
        result.put("name", getName());
        result.put("logstore", getLogstore());
        result.put("shipperName", getShipperName());
        return result.toJSONString();
    }

    private void checkStringEmpty(String name, String value) throws IllegalArgumentException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(name + " is null/empty");
        }
    }

    public static ShipperMigration extractGetMigration(JSONObject body, String requestId) throws LogException {
        ShipperMigration migration = new ShipperMigration("", "", "");
        migration.setName(body.getString("name"));
        migration.setLogstore(body.getString("logstore"));
        migration.setShipperName(body.getString("shipperName"));
        migration.setShipperType(body.getString("shipperType"));
        migration.setExportLink(body.getString("exportLink"));
        migration.setState(body.getString("state"));
        migration.setRetryCount(body.getInteger("retryCount"));
        migration.setMessage(body.getString("message"));
        migration.setUserConf(body.getString("userConf"));
        migration.setCreateTime(body.getLong("createTime"));
        migration.setUpdateTime(body.getLong("updateTime"));
        return migration;
    }

    public static ShipperMigration extractListMigration(JSONObject body, String requestId) throws LogException {
        ShipperMigration migration = new ShipperMigration("", "", "");
        migration.setName(body.getString("name"));
        migration.setLogstore(body.getString("logstore"));
        migration.setShipperName(body.getString("shipperName"));
        migration.setShipperType(body.getString("shipperType"));
        migration.setExportLink(body.getString("exportLink"));
        migration.setState(body.getString("state"));
        migration.setMessage(body.getString("message"));
        return migration;
    }

    public static List<ShipperMigration> extractMigrations(JSONObject body, String requestId) throws LogException {
        List<ShipperMigration> migrations = new ArrayList<ShipperMigration>();
        JSONArray array = new JSONArray();
        try {
            array = body.getJSONArray("migrations");
            if (array == null) {
                return migrations;
            }
            for (int index = 0; index < array.size(); index++) {
                JSONObject jsonObject = array.getJSONObject(index);
                if (jsonObject == null) {
                    continue;
                }
                migrations.add(ShipperMigration.extractListMigration(jsonObject, requestId));
            }
        } catch (JSONException e) {
            throw new LogException(ErrorCodes.BAD_RESPONSE, "The response is not valid config json array string : " + array.toString(), e, requestId);
        }
        return migrations;
    }
}
