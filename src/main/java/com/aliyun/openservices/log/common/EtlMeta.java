package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class EtlMeta implements Serializable {

    private static final long serialVersionUID = -4940373026567060213L;
    private String metaName = null;
    private String metaKey = null;
    private String metaTag = null;
    private JSONObject metaValue = null;
    private long createTime; //for ListEtlMetaReponse, the field is not used when create/update etlMeta
    private long lastModifyTime; //for ListEtlMetaReponse, the field is not used when create/update etlMeta
    private boolean enable;

    public EtlMeta() {
        this.enable = true;
    }

    public EtlMeta(String metaName, String metaKey, String metaTag) {
        this.metaName = metaName;
        this.metaKey = metaKey;
        this.metaTag = metaTag;
        this.enable = true;
    }

    public EtlMeta(String metaName, String metaKey, String metaTag, JSONObject metaValue, boolean enable) {
        this.metaName = metaName;
        this.metaKey = metaKey;
        this.metaTag = metaTag;
        this.metaValue = metaValue;
        this.enable = enable;
    }

    public EtlMeta(String metaName, String metaKey, boolean enable) {
        this.metaName = metaName;
        this.metaKey = metaKey;
        this.enable = enable;
    }

    public void setMetaName(String metaName) {
        this.metaName = metaName;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public void setMetaTag(String metaTag) {
        this.metaTag = metaTag;
    }

    public void setMetaValue(JSONObject metaValue) {
        this.metaValue = metaValue;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getMetaName() {
        return metaName;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public String getMetaTag() {
        return metaTag;
    }

    public JSONObject getMetaValue() {
        return metaValue;
    }

    public boolean isEnable() {
        return enable;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public JSONObject toJsonObject() {
        JSONObject etlMetaJson = new JSONObject();
        etlMetaJson.put(Consts.ETL_META_NAME, this.metaName);
        etlMetaJson.put(Consts.ETL_META_KEY, this.metaKey);
        if (this.metaTag != null) {
            etlMetaJson.put(Consts.ETL_META_TAG, this.metaTag);
        }
        if (this.metaValue != null) {
            etlMetaJson.put(Consts.ETL_META_VALUE, this.metaValue);
        }
        etlMetaJson.put(Consts.ETL_META_ENABLE, this.enable);
        return etlMetaJson;
    }

    public void fromJsonObject(JSONObject etlMetaJson) throws LogException {
        try {
            this.metaName = etlMetaJson.getString(Consts.ETL_META_NAME);
            this.metaKey = etlMetaJson.getString(Consts.ETL_META_KEY);
            this.metaTag = etlMetaJson.getString(Consts.ETL_META_TAG);
            // For etl metas created by logging, EtlMetaValue may ends with new line.
            final String value = etlMetaJson.getString(Consts.ETL_META_VALUE);
            this.metaValue = JSONObject.fromObject(value.trim());
            if (etlMetaJson.has(Consts.ETL_META_CREATE_TIME)) {
                this.createTime = etlMetaJson.getLong(Consts.ETL_META_CREATE_TIME);
            } else {
                this.createTime = 0;
            }
            if (etlMetaJson.has(Consts.ETL_META_LAST_MODIFY_TIME)) {
                this.lastModifyTime = etlMetaJson.getLong(Consts.ETL_META_LAST_MODIFY_TIME);
            } else {
                this.lastModifyTime = 0;
            }
            if (etlMetaJson.has(Consts.ETL_META_ENABLE)) {
                this.enable = etlMetaJson.getBoolean(Consts.ETL_META_ENABLE);
            } else {
                this.enable = true;
            }
        } catch (JSONException e) {
            throw new LogException("BadResponse", e.getMessage(), e, "");
        }
    }

    public void checkForCreate() throws IllegalArgumentException {
        if (this.metaName == null || this.metaKey == null
        || this.metaTag == null || this.metaValue == null) {
            throw new IllegalArgumentException("metaName/metaKey/metaTag/metaValue is uninitialized");
        }
    }

    public void checkForUpdate() throws IllegalArgumentException {
        if (this.metaName == null || this.metaKey == null) {
            throw new IllegalArgumentException("metaName/metaKey is uninitialized");
        }
    }
}
