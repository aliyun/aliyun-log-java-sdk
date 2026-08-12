package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class TopostoreRelation implements Serializable, JsonSerializable, JsonDeserializable {
    private String relationId = "";
    private String relationType = "";
    private String srcNodeId = null;
    private String dstNodeId = null;
    private String property = null;
    private String description = null;
    private String displayName = null;
    private long createTime = 0;
    private long lastModifyTime = 0;

    public long getCreateTime() {
        return createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public TopostoreRelation(String relationId, String relationType, String srcNodeId, String dstNodeId) {
        this(relationId, relationType, srcNodeId, dstNodeId, null, null);
    }

    public TopostoreRelation(String relationId, String relationType, String srcNodeId, String dstNodeId, String property) {
        this(relationId, relationType, srcNodeId, dstNodeId, property, null);
    }

    public TopostoreRelation(String relationId, String relationType, String srcNodeId, String dstNodeId, String property, String description) {
        this.relationId = relationId;
        this.relationType = relationType;
        this.srcNodeId = srcNodeId;
        this.dstNodeId = dstNodeId;
        this.property = property;
        this.description = description;
    }

    public TopostoreRelation() {
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }


    public String getSrcNodeId() {
        return srcNodeId;
    }

    public void setSrcNodeId(String srcNodeId) {
        this.srcNodeId = srcNodeId;
    }


    public String getDstNodeId() {
        return dstNodeId;
    }

    public void setDstNodeId(String dstNodeId) {
        this.dstNodeId = dstNodeId;
    }


    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setProperty(Map<String, String> properties) {
        if(properties == null){
            return;
        }
        JSONObject proObj = new JSONObject();
        for(Map.Entry<String, String> kv : properties.entrySet()){
            proObj.put(kv.getKey(), kv.getValue());
        }
        setProperty(proObj.toString());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @InternalApi
    public JSONObject toJsonObject() throws LogException {
        JSONObject result = new JSONObject();
        result.put(Consts.TOPOSTORE_RELATION_ID, getRelationId());
        result.put(Consts.TOPOSTORE_RELATION_TYPE, getRelationType());
        result.put(Consts.TOPOSTORE_RELATION_SRC_NODE_ID, getSrcNodeId());
        result.put(Consts.TOPOSTORE_RELATION_DST_NODE_ID, getDstNodeId());

        if (property != null) {
            result.put(Consts.TOPOSTORE_RELATION_PROPERTY, getProperty());
        }

        if (description != null) {
            result.put(Consts.TOPOSTORE_RELATION_DESCRIPTION, getDescription());
        }

        if (displayName != null) {
            result.put(Consts.TOPOSTORE_RELATION_DISPLAY_NAME, getDisplayName());
        }

        return result;
    }


    @InternalApi
    public void fromJsonObject(JSONObject dict) throws LogException {
        setRelationId(dict.getString(Consts.TOPOSTORE_RELATION_ID));
        setRelationType(dict.getString(Consts.TOPOSTORE_RELATION_TYPE));
        setSrcNodeId(dict.getString(Consts.TOPOSTORE_RELATION_SRC_NODE_ID));
        setDstNodeId(dict.getString(Consts.TOPOSTORE_RELATION_DST_NODE_ID));

        // property
        if (dict.containsKey(Consts.TOPOSTORE_RELATION_PROPERTY)) {
            setProperty(dict.getString(Consts.TOPOSTORE_RELATION_PROPERTY));
        }

        // displayName
        if (dict.containsKey(Consts.TOPOSTORE_RELATION_DISPLAY_NAME)) {
            setDisplayName(dict.getString(Consts.TOPOSTORE_RELATION_DISPLAY_NAME));
        }

        // description
        if (dict.containsKey(Consts.TOPOSTORE_RELATION_DESCRIPTION)) {
            setDescription(dict.getString(Consts.TOPOSTORE_RELATION_DESCRIPTION));
        }

        if (dict.containsKey(Consts.TOPOSTORE_RELATION_CREATE_TIME)) {
            createTime = dict.getIntValue(Consts.TOPOSTORE_RELATION_CREATE_TIME);
        }

        if (dict.containsKey(Consts.TOPOSTORE_LAST_MODIFY_TIME)) {
            lastModifyTime = dict.getIntValue(Consts.TOPOSTORE_LAST_MODIFY_TIME);
        }
    }

    public void fromJsonString(String content) throws LogException {
        JSONObject dict = JSONObject.parseObject(content);
        fromJsonObject(dict);
    }

    public void checkForCreate() throws IllegalArgumentException {
        if (relationId == null || relationId.isEmpty()) {
            throw new IllegalArgumentException("topostore relation id is null/empty");
        }

        if (relationType == null || relationType.isEmpty()) {
            throw new IllegalArgumentException("topostore relation type is null/empty");
        }

        if (property != null && property.length()>0) {
            try {
                JSONObject.parseObject(property);
            } catch (JSONException e) {
                throw new IllegalArgumentException("topostore relation property not valid json");
            }
        }
    }

    public void checkForUpsert() throws IllegalArgumentException {
        checkForCreate();
    }

    public void checkForUpdate() throws IllegalArgumentException {
        checkForCreate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TopostoreRelation)) {
            return false;
        }

        TopostoreRelation that = (TopostoreRelation) o;

        if (relationId != null ? !relationId.equals(that.relationId) : that.relationId != null) {
            return false;
        }
        return relationType != null ? relationType.equals(that.relationType) : that.relationType == null;
    }

    @Override
    public int hashCode() {
        int result = relationId != null ? relationId.hashCode() : 0;
        result = 31 * result + (relationType != null ? relationType.hashCode() : 0);
        return result;
    }
}
