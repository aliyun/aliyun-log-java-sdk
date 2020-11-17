package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ConsumerGroupHeartBeatRequest extends ConsumerGroupRequest {
    /**
     *
     */
    private static final long serialVersionUID = 7110490639104895609L;
    private List<Integer> shards;

    /**
     * @param project       project name
     * @param logstore      logstore name
     * @param consumerGroup consumer group name
     * @param consumer      consumer name
     * @param shards        the shards hold by the consumer
     */
    public ConsumerGroupHeartBeatRequest(String project, String logstore, String consumerGroup, String consumer, List<Integer> shards) {
        super(project, logstore);
        super.SetParam("type", "heartbeat");
        super.SetParam("consumer", consumer);
        this.shards = shards;
    }

    @Deprecated
    public List<Integer> GetShards() {
        return shards;
    }

    @Deprecated
    public void SetShards(ArrayList<Integer> shards) {
        this.shards = shards;
    }

    public List<Integer> getShards() {
        return shards;
    }

    public void setShards(List<Integer> shards) {
        this.shards = shards;
    }

    public String GetRequestBody() {
        JSONArray array = new JSONArray();
        array.addAll(shards);
        return array.toString();
    }
}
