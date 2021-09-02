package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;

public class ProjectConsumerGroupUpdateCheckPointRequest extends Request {

    private static final long serialVersionUID = -45549621456203604L;

    private String consumerGroup;
    private String logStore;
    private int shard;
    private String checkPoint;

    public ProjectConsumerGroupUpdateCheckPointRequest(
            String project,
            String consumerGroup,
            String consumer,
            String logStore,
            int shard,
            String checkPoint,
            boolean forceSuccess) {
        super(project);
        this.consumerGroup = consumerGroup;
        this.logStore = logStore;
        this.shard = shard;
        this.checkPoint = checkPoint;

        super.SetParam("type", "checkpoint");
        super.SetParam("consumer", consumer);
        super.SetParam("forceSuccess", forceSuccess ? "true" : "false");
    }

    public String GetRequestBody() {
        JSONObject dict = new JSONObject();
        dict.put("logstore", logStore);
        dict.put("shard", shard);
        dict.put("checkpoint", checkPoint);
        return dict.toString();
    }
}