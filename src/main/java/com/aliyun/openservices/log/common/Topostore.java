package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.internal.ErrorCodes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Topostore implements Serializable {
    private String name = "";
    private String tag = "{}";
    private String schema = null;
    private String acl = null;
    private String description = null;
    private String extInfo = null;
    private long createTime = 0;
    private long lastModifyTime = 0;

    public long getCreateTime() {
        return createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public Topostore(String name, String tag, String schema, String acl, String description, String extInfo) {
        this.name = name;
        this.tag = tag;
        this.schema = schema;
        this.acl = acl;
        this.description = description;
        this.extInfo = extInfo;
    }

    public Topostore(String name, String tag) {
        this(name, tag, null, null, null, null);
    }

    public Topostore(String name) {
        this(name, "{}", null, null, null, null);
    }

    public Topostore() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTag(Map<String, String> tag) {
        if(tag == null){
            return;
        }
        JSONObject tagObj = new JSONObject();
        for(Map.Entry<String, String> kv : tag.entrySet()){
            tagObj.put(kv.getKey(), kv.getValue());
        }
        setTag(tagObj.toJSONString());
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public JSONObject ToJsonObject() throws LogException {
        JSONObject result = new JSONObject();
        result.put(Consts.TOPOSTORE_NAME, getName());
        result.put(Consts.TOPOSTORE_TAG, getTag());
        if (schema != null) {
            result.put(Consts.TOPOSTORE_SCHEMA, getSchema());
        }

        if (description != null) {
            result.put(Consts.TOPOSTORE_DESCRIPTION, getDescription());
        }

        if (extInfo != null) {
            result.put(Consts.TOPOSTORE_EXTINFO, getExtInfo());
        }

        if (acl != null) {
            result.put(Consts.TOPOSTORE_ACL, getAcl());
        }
        return result;
    }

    public String ToJsonString() throws LogException {
        return ToJsonObject().toJSONString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        setName(dict.getString(Consts.TOPOSTORE_NAME));
        setTag(dict.getString(Consts.TOPOSTORE_TAG));

        // description
        if (dict.containsKey(Consts.TOPOSTORE_DESCRIPTION)) {
            setDescription(dict.getString(Consts.TOPOSTORE_DESCRIPTION));
        }

        // schema
        if (dict.containsKey(Consts.TOPOSTORE_SCHEMA)) {
            setSchema(dict.getString(Consts.TOPOSTORE_SCHEMA));
            try {
                JSONObject.parseObject(dict.getString(Consts.TOPOSTORE_SCHEMA));
            } catch (JSONException e) {
                throw new LogException(ErrorCodes.BAD_RESPONSE, "response topostore schema is not valid json", e,
                        e.getMessage());
            }
        }

        // acl
        if (dict.containsKey(Consts.TOPOSTORE_ACL)) {
            setAcl(dict.getString(Consts.TOPOSTORE_ACL));
            try {
                JSONObject.parseObject(dict.getString(Consts.TOPOSTORE_ACL));
            } catch (JSONException e) {
                throw new LogException(ErrorCodes.BAD_RESPONSE, "response topostore acl is not valid json", e,
                        e.getMessage());
            }
        }

        // extInfo
        if (dict.containsKey(Consts.TOPOSTORE_EXTINFO)) {
            setExtInfo(dict.getString(Consts.TOPOSTORE_EXTINFO));
        }

        if (dict.containsKey(Consts.TOPOSTORE_CREATE_TIME)) {
            createTime = dict.getIntValue(Consts.TOPOSTORE_CREATE_TIME);
        }

        if (dict.containsKey(Consts.TOPOSTORE_LAST_MODIFY_TIME)) {
            lastModifyTime = dict.getIntValue(Consts.TOPOSTORE_LAST_MODIFY_TIME);
        }
    }

    public void FromJsonString(String content) throws LogException {
        JSONObject dict = JSONObject.parseObject(content);
        FromJsonObject(dict);
    }

    public void checkForCreate() throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("topostore name is null/empty");
        }

        if (schema != null) {
            try {
                JSONObject.parseObject(schema);
            } catch (JSONException e) {
                throw new IllegalArgumentException("topostore schema not valid json");
            }
        }

        if (acl != null) {
            try {
                JSONObject.parseObject(acl);
            } catch (JSONException e) {
                throw new IllegalArgumentException("topostore acl not valid json");
            }
        }
    }

    public void checkForUpsert() throws IllegalArgumentException {
        checkForCreate();
    }

    public void checkForUpdate() throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("topostore name is null/empty");
        }

        if (schema != null) {
            try {
                JSONObject.parseObject(schema);
            } catch (JSONException e) {
                throw new IllegalArgumentException("topostore schema not valid json");
            }
        }

        if (acl != null) {
            try {
                JSONObject.parseObject(acl);
            } catch (JSONException e) {
                throw new IllegalArgumentException("topostore acl not valid json");
            }
        }
    }
}
