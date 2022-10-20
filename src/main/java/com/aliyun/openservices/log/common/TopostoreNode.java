package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TopostoreNode implements Serializable {
    private String nodeId = "";
    private String nodeType = "";
    private String property = null;
    private String description = null;
    private String displayName = null;
    private long createTime = 0;
    private long lastModifyTime = 0;

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public TopostoreNode(String nodeId, String nodeType) {
        this(nodeId, nodeType, null, null);
    }

    public TopostoreNode(String nodeId, String nodeType, String property) { 
        this(nodeId, nodeType, property, null);
    }

    public TopostoreNode(String nodeId, String nodeType, String property, String description) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.property = property;
        this.description = description;
    }

    public TopostoreNode() {
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
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
        setProperty(proObj.toJSONString());
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

    public JSONObject ToJsonObject() throws LogException {
        JSONObject result = new JSONObject();
        result.put(Consts.TOPOSTORE_NODE_ID, getNodeId());
        result.put(Consts.TOPOSTORE_NODE_TYPE, getNodeType());
        if (property != null) {
            result.put(Consts.TOPOSTORE_NODE_PROPERTY, getProperty());
        }

        if (description != null) {
            result.put(Consts.TOPOSTORE_NODE_DESCRIPTION, getDescription());
        }

        if (displayName != null) {
            result.put(Consts.TOPOSTORE_NODE_DISPLAY_NAME, getDisplayName());
        }

        return result;
    }

    public String ToJsonString() throws LogException {
        return ToJsonObject().toJSONString();
    }

    public void FromJsonObject(JSONObject dict) throws LogException {
        setNodeId(dict.getString(Consts.TOPOSTORE_NODE_ID));
        setNodeType(dict.getString(Consts.TOPOSTORE_NODE_TYPE));

        // property
        if (dict.containsKey(Consts.TOPOSTORE_NODE_PROPERTY)) {
            setProperty(dict.getString(Consts.TOPOSTORE_NODE_PROPERTY));
        }

        // displayName
        if (dict.containsKey(Consts.TOPOSTORE_NODE_DISPLAY_NAME)) {
            setDisplayName(dict.getString(Consts.TOPOSTORE_NODE_DISPLAY_NAME));
        }

        // description
        if (dict.containsKey(Consts.TOPOSTORE_NODE_DESCRIPTION)) {
            setDescription(dict.getString(Consts.TOPOSTORE_NODE_DESCRIPTION));
        }

        if (dict.containsKey(Consts.TOPOSTORE_NODE_CREATE_TIME)) {
            createTime = dict.getIntValue(Consts.TOPOSTORE_NODE_CREATE_TIME);
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
        if (nodeId == null || nodeId.isEmpty()) {
            throw new IllegalArgumentException("topostore node id is null/empty");
        }

        if (nodeType == null || nodeType.isEmpty()) {
            throw new IllegalArgumentException("topostore node type is null/empty");
        }

        if (property != null) {
            try {
                JSONObject.parseObject(property);
            } catch (JSONException e) {
                throw new IllegalArgumentException("topostore node property not valid json");
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
        if (!(o instanceof TopostoreNode)) {
            return false;
        }

        TopostoreNode that = (TopostoreNode) o;

        if (nodeId != null ? !nodeId.equals(that.nodeId) : that.nodeId != null) {
            return false;
        }
        return nodeType != null ? nodeType.equals(that.nodeType) : that.nodeType == null;
    }

    @Override
    public int hashCode() {
        int result = nodeId != null ? nodeId.hashCode() : 0;
        result = 31 * result + (nodeType != null ? nodeType.hashCode() : 0);
        return result;
    }
}
