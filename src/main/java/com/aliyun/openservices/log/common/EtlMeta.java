package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.exception.LogException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.Serializable;

public class EtlMeta implements Serializable {

    private static final long serialVersionUID = -4940373026567060213L;
    private String metaName;
    private String metaKey;
    private String metaTag;
    private JSONObject metaValue;
    private long createTime; //for ListEtlMetaReponse, the field is not used when create/update etlMeta
    private long lastModifyTime; //for ListEtlMetaReponse, the field is not used when create/update etlMeta

    public EtlMeta() {
    }

    public EtlMeta(String metaName, String metaKey, String metaTag) {
        this.metaName = metaName;
        this.metaKey = metaKey;
        this.metaTag = metaTag;
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

    public long getCreateTime() {
        return createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setMetaValue(JSONObject metaValue) {
        this.metaValue = metaValue;
    }

    public JSONObject toJsonObject() {
        JSONObject etlMetaJson = new JSONObject();
        etlMetaJson.put(Consts.ETL_META_NAME, this.metaName);
        etlMetaJson.put(Consts.ETL_META_KEY, this.metaKey);
        etlMetaJson.put(Consts.ETL_META_TAG, this.metaTag);
        etlMetaJson.put(Consts.ETL_META_VALUE, this.metaValue);
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
        } catch (JSONException e) {
            throw new LogException("BadResponse", e.getMessage(), e, "");
        }
    }
}
