package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class ProjectConsumerGroup implements Serializable {
    private String consumerGroupName;
    private String logStoreExpr;
    private int timeout;
    private boolean inOrder;

    /**
     * @param consumerGroupName consumer group name
     * @param logStoreExpr      log store glob expression
     * @param timeout           if the time interval of a consumer's heartbeat exceed this value in second,  the consumer will be deleted.
     * @param inOrder           consume data in oder or not
     */
    public ProjectConsumerGroup(String consumerGroupName, String logStoreExpr, int timeout, boolean inOrder) {
        this.consumerGroupName = consumerGroupName;
        this.logStoreExpr = logStoreExpr;
        this.timeout = timeout;
        this.inOrder = inOrder;
    }

    public String getConsumerGroupName() {
        return consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName) {
        this.consumerGroupName = consumerGroupName;
    }

    public String getLogStoreExpr() {
        return logStoreExpr;
    }

    public void setLogStoreExpr(String logStoreExpr) {
        this.logStoreExpr = logStoreExpr;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isInOrder() {
        return inOrder;
    }

    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

    public JSONObject ToRequestJson() {
        JSONObject logStoreDict = new JSONObject();
        logStoreDict.put("consumerGroup", getConsumerGroupName());
        logStoreDict.put("timeout", getTimeout());
        logStoreDict.put("order", isInOrder());
        logStoreDict.put("logstoreName", getLogStoreExpr());

        return logStoreDict;
    }

    public String ToRequestString() {
        return ToRequestJson().toString();
    }

    @Override
    public String toString() {
        return "ProjectConsumerGroup [consumerGroupName=" + consumerGroupName
                + ", expr=" + logStoreExpr + ", timeout=" + timeout + ", inOrder=" + inOrder + "]";
    }
}