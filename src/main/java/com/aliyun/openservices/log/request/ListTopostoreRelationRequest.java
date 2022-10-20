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

public class ListTopostoreRelationRequest extends TopostoreRequest {

    private String topostoreName;
    private List<String> relationIds = new ArrayList<String>();
    private List<String> relationTypes = new ArrayList<String>();
    private List<String> srcNodeIds = new ArrayList<String>();
    private List<String> dstNodeIds = new ArrayList<String>();
    private String propertyKey = "";
    private String propertyValue = "";
    private Integer offset=0;
    private Integer size=200;
    private Map<String, String> properties =  new HashMap<String,String>();

    public Map<String,String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String,String> properties) {
        if(properties==null){
            return;
        }
        this.properties = properties;
    }

    public ListTopostoreRelationRequest() {
    }


    public ListTopostoreRelationRequest(String topostoreName) {
        this(topostoreName, new ArrayList<String>(),new ArrayList<String>(), "", "", 0, 200 );
    }

    public ListTopostoreRelationRequest(String topostoreName, List<String> relationIds) {
        this(topostoreName, relationIds,new ArrayList<String>(), "", "", 0, 200 );
    }

    public ListTopostoreRelationRequest(String topostoreName, List<String> relationIds, List<String> relationTypes, 
        String propertyKey, String propertyValue, Integer offset, Integer size) {
        this.topostoreName = topostoreName;
        if(relationIds != null ){
            this.relationIds = relationIds;
        }
        if(relationTypes!=null){
            this.relationTypes = relationTypes;
        }
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
        this.offset = offset;
        this.size = size;
    }

    public ListTopostoreRelationRequest(String topostoreName,String propertyKey, String propertyValue) {
        this(topostoreName, new ArrayList<String>(),new ArrayList<String>(), propertyKey, propertyValue, 0, 200 );
    }

    public String getTopostoreName() {
        return this.topostoreName;
    }

    public void setTopostoreName(String topostoreName) {
        this.topostoreName = topostoreName;
    }

    public List<String> getRelationIds() {
        return this.relationIds;
    }

    public void setRelationIds(List<String> relationIds) {
        if(relationIds == null ) {
            return ;
        }
        this.relationIds = relationIds;
    }

    public ListTopostoreRelationRequest addProperties(String key, String value){
        this.properties.put(key, value);
        return this;
    }

    public void setRelationIds(String... relationIds){
        this.relationIds = Arrays.asList(relationIds);
    }

    public ListTopostoreRelationRequest addRelationId(String relationId){
        this.relationIds.add(relationId);
        return this;
    }

    public void setRelationTypes(String... relationTypes){
        this.relationTypes = Arrays.asList(relationTypes);
    }

    public ListTopostoreRelationRequest addRelationType(String relationType){
        this.relationTypes.add(relationType);
        return this;
    }

    public void setSrcNodeIds(String... srcNodeIds){
        this.srcNodeIds = Arrays.asList(srcNodeIds);
    }

    public ListTopostoreRelationRequest addSrcNodeId(String srcNodeId){
        this.srcNodeIds.add(srcNodeId);
        return this;
    }

    public void setDstNodeIds(String... dstNodeIds){
        this.dstNodeIds = Arrays.asList(dstNodeIds);
    }

    public ListTopostoreRelationRequest addDstNodeId(String dstNodeId){
        this.dstNodeIds.add(dstNodeId);
        return this;
    }

    public List<String> getRelationTypes() {
        return this.relationTypes;
    }

    public void setRelationTypes(List<String> relationTypes) {
        if(relationTypes == null ) {
            return ;
        }
        this.relationTypes = relationTypes;
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

    public List<String> getSrcNodeIds() {
        return this.srcNodeIds;
    }

    public void setSrcNodeIds(List<String> srcNodeIds) {
        if(srcNodeIds == null ) {
            return ;
        }
        this.srcNodeIds = srcNodeIds;
    }

    public List<String> getDstNodeIds() {
        return this.dstNodeIds;
    }

    public void setDstNodeIds(List<String> dstNodeIds) {
        if(dstNodeIds == null ) {
            return ;
        }
        this.dstNodeIds = dstNodeIds;
    }
    
    @Override
    public Map<String, String> GetAllParams() {
        if (offset != null) {
            SetParam(Consts.CONST_OFFSET, offset.toString());
        }

        if (size != null) {
            SetParam(Consts.CONST_SIZE, size.toString());
        }

        if(relationIds != null && !relationIds.isEmpty()){
            SetParam(Consts.TOPOSTORE_RELATION_ID_LIST, Utils.join(",", Utils.removeNullItems(relationIds)));
        }

        if(relationTypes != null && !relationTypes.isEmpty()){
            SetParam(Consts.TOPOSTORE_RELATION_TYPE_LIST, Utils.join(",", Utils.removeNullItems(relationTypes)));
        }

        if(srcNodeIds != null && !srcNodeIds.isEmpty()){
            SetParam(Consts.TOPOSTORE_RELATION_SRC_NODE_ID_LIST, Utils.join(",", Utils.removeNullItems(srcNodeIds)));
        }

        if(dstNodeIds != null && !dstNodeIds.isEmpty()){
            SetParam(Consts.TOPOSTORE_RELATION_DST_NODE_ID_LIST, Utils.join(",", Utils.removeNullItems(dstNodeIds)));
        }

        if(propertyKey!=null&&propertyKey.length()>0){
            SetParam(Consts.TOPOSTORE_RELATION_PROPERTY_KEY, propertyKey);
        }

        if(propertyValue!=null&&propertyValue.length()>0){
            SetParam(Consts.TOPOSTORE_RELATION_PROPERTY_VALUE, propertyValue);
        }

        if(properties!=null && !properties.isEmpty()){
            JSONObject proObj = new JSONObject();
            for(Map.Entry<String, String> kv : properties.entrySet()){
                proObj.put(kv.getKey(), kv.getValue());
            }
            
            try{
                SetParam(Consts.TOPOSTORE_RELATION_PROPERTIES, URLEncoder.encode(new String(
                    Base64.getEncoder().encodeToString(proObj.toJSONString().getBytes())), "utf-8"));
            } catch(UnsupportedEncodingException e){
                throw new RuntimeException(e);
            }
        }
        return super.GetAllParams();
    }

}
