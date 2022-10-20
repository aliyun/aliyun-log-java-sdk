package com.aliyun.openservices.log.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Consts;

public class ListTopostoreNodeRelationRequest{
    private String topostoreName;
    // node selection
    private List<String> nodeIds = new ArrayList<String>();
    private List<String> nodeTypes = new ArrayList<String>();
    private Map<String, String> nodeProperties = new HashMap<String,String>();

    // relation selection
    private List<String> relationTypes;
    private long relationDepth = 0;
    private String relationDirection = Consts.TOPOSTORE_RELATION_DIRECTION_BOTH;
    private long from;
    private long to;

    // extParams;
    private Map<String, String> params = new HashMap<String,String>();

    public ListTopostoreNodeRelationRequest() {
    }

    public ListTopostoreNodeRelationRequest(String topostoreName)  {
        this(topostoreName, new ArrayList<String>(), new ArrayList<String>(), new HashMap<String,String>(), new ArrayList<String>(), 0, Consts.TOPOSTORE_RELATION_DIRECTION_BOTH);
    }

    public ListTopostoreNodeRelationRequest(String topostoreName, List<String> nodeIds)  {
        this(topostoreName, nodeIds, new ArrayList<String>(), new HashMap<String,String>(), new ArrayList<String>(), 0, Consts.TOPOSTORE_RELATION_DIRECTION_BOTH);
    }

    public ListTopostoreNodeRelationRequest(String topostoreName, List<String> nodeIds, int relationDepth)  {
        this(topostoreName, nodeIds, new ArrayList<String>(), new HashMap<String,String>(), new ArrayList<String>(), relationDepth, Consts.TOPOSTORE_RELATION_DIRECTION_BOTH);
    }

    public ListTopostoreNodeRelationRequest(String topostoreName, List<String> nodeIds, List<String> nodeTypes,
        Map<String, String> nodeProperities, List<String> relationTypes, int relationDepth, String relationDirection)  {
        this.topostoreName = topostoreName;
        if(nodeIds!=null){
            this.nodeIds = nodeIds;
        }
        if(nodeTypes!=null){
            this.nodeTypes = nodeTypes;
        }
        if(nodeProperities!=null){
            this.nodeProperties = nodeProperities;
        }
        if(relationTypes!=null){
            this.relationTypes = relationTypes;
        }
        this.relationDepth = relationDepth;
        this.relationDirection = relationDirection;
    }

    public long getFrom() {
        return this.from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return this.to;
    }

    public void setTo(long to) {
        this.to = to;
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

    public void setNodeIds(String... nodeIds ){
        this.nodeIds = Arrays.asList(nodeIds);
    }

    public void setNodeTypes(String... nodeTypes ){
        this.nodeTypes = Arrays.asList(nodeTypes);
    }

    public void setRelationTypes(String... relationTypes ){
        this.relationTypes = Arrays.asList(relationTypes);
    }

    public ListTopostoreNodeRelationRequest addNodeProperty(String key, String value){
        this.nodeProperties.put(key, value);
        return this;
    }

    public void setNodeIds(List<String> nodeIds) {
        if(nodeIds == null){
            return;
        }
        this.nodeIds = nodeIds;
    }

    public void SetParam(String key, String value) {
        this.params.put(key, value);
    }

    public Map<String, String> GetParam() {
        return this.params;
    }
    
    public List<String> getNodeTypes() {
        return this.nodeTypes;
    }

    public void setNodeTypes(List<String> nodeTypes) {
        if(nodeTypes == null){
            return;
        }
        this.nodeTypes = nodeTypes;
    }

    public Map<String, String> getNodeProperities() {
        return this.nodeProperties;
    }

    public void setNodeProperities(Map<String, String> nodeProperities) {
        if(nodeProperities == null){
            return;
        }
        this.nodeProperties = nodeProperities;
    }

    public List<String> getRelationTypes() {
        return this.relationTypes;
    }

    public void setRelationTypes(List<String> relationTypes) {
        this.relationTypes = relationTypes;
    }

    public long getDepth() {
        return this.relationDepth;
    }

    public void setDepth(long depth) {
        this.relationDepth = depth;
    }

    public String getDirection() {
        return this.relationDirection;
    }

    public void setDirection(String direction) {
        this.relationDirection = direction;
    }

}
