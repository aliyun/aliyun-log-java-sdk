package com.aliyun.openservices.log.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.util.Utils;

public class ListTopostoreNodeRequest extends TopostoreRequest {

    private String topostoreName;
    private List<String> nodeIds = new ArrayList<String>();
    private List<String> nodeTypes = new ArrayList<String>();
    private String propertyKey;
    private String propertyValue;
    private Map<String, String> properties = new HashMap<String,String>();

    public Map<String,String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String,String> properties) {
        this.properties = properties;
    }

    private Integer offset=0;
    private Integer size=200;

    public ListTopostoreNodeRequest() {
    }

    public ListTopostoreNodeRequest(String topostoreName) {
        this(topostoreName, new ArrayList<String>(), new ArrayList<String>(), "", "", 0, 200);
    }

    public ListTopostoreNodeRequest(String topostoreName, List<String> nodeIds) {
        this(topostoreName, nodeIds, new ArrayList<String>(), "", "", 0, 200);
    }

    public ListTopostoreNodeRequest(String topostoreName, List<String> nodeIds, List<String> nodeTypes, 
        String propertyKey, String propertyValue, Integer offset, Integer size) {
        this.topostoreName = topostoreName;
        this.nodeIds = nodeIds;
        this.nodeTypes = nodeTypes;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
        this.offset = offset;
        this.size = size;
    }

    public ListTopostoreNodeRequest(String topostoreName,String propertyKey, String propertyValue) {
        this(topostoreName, new ArrayList<String>(), new ArrayList<String>(), propertyKey, propertyValue, 0, 200);
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public List<String> getNodeIds() {
        return this.nodeIds;
    }

    public void setNodeIds(List<String> nodeIds) {
        if (nodeIds == null) {
            return ;
        }
        this.nodeIds = nodeIds;
    }

    public void setNodeIds(String... nodeIds){
        this.nodeIds = Arrays.asList(nodeIds);
    }

    public ListTopostoreNodeRequest addNodeIds(String nodeId){
        this.nodeIds.add(nodeId);
        return this;
    }

    public void setNodeTypes(String... nodeTypes){
        this.nodeTypes = Arrays.asList(nodeTypes);
    }

    public List<String> getNodeTypes() {
        return this.nodeTypes;
    }

    public void setNodeTypes(List<String> nodeTypes) {
        if(nodeTypes == null ){
            return ;
        }
        this.nodeTypes = nodeTypes;
    }

    public ListTopostoreNodeRequest addProperties(String key, String value){
        this.properties.put(key, value);
        return this;
    }

    public String getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
    
    @Override
    public Map<String, String> GetAllParams() {
        if (offset != null) {
            SetParam(Consts.CONST_OFFSET, offset.toString());
        }

        if (size != null) {
            SetParam(Consts.CONST_SIZE, size.toString());
        }

        if(nodeIds != null && !nodeIds.isEmpty()){
            SetParam(Consts.TOPOSTORE_NODE_ID_LIST, Utils.join(",", Utils.removeNullItems(nodeIds)));
        }

        if(nodeTypes != null && !nodeTypes.isEmpty()){
            SetParam(Consts.TOPOSTORE_NODE_TYPE_LIST, Utils.join(",", Utils.removeNullItems(nodeTypes)));
        }

        if(propertyKey!=null && propertyKey.length()>0){
            SetParam(Consts.TOPOSTORE_NODE_PROPERTY_KEY, propertyKey);
        }

        if(propertyValue!= null && propertyValue.length()>0){
            SetParam(Consts.TOPOSTORE_NODE_PROPERTY_VALUE, propertyValue);
        }

        if(properties!=null && !properties.isEmpty()){

            JSONObject proObj = new JSONObject();
            for(Map.Entry<String, String> kv : properties.entrySet()){
                proObj.put(kv.getKey(), kv.getValue());
            }
            
            try{
                SetParam(Consts.TOPOSTORE_NODE_PROPERTIES, URLEncoder.encode(new String(
                    Base64.getEncoder().encodeToString(proObj.toJSONString().getBytes())), "utf-8"));
            } catch(UnsupportedEncodingException e){
                throw new RuntimeException(e);
            }
           
        }
        return super.GetAllParams();
    }

}
