package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.internal.json.JsonCodec;

import java.util.ArrayList;
import java.util.Map;

public class ProjectConsumerGroupHeartBeatRequest extends Request {
    private static final long serialVersionUID = 723075705068277623L;
    private Map<String, ArrayList<Integer>> logStoreShards;

    public ProjectConsumerGroupHeartBeatRequest(String project, String consumer, Map<String, ArrayList<Integer>> logStoreShards) {
        super(project);
        this.logStoreShards = logStoreShards;
        super.SetParam("type", "heartbeat");
        super.SetParam("consumer", consumer);
    }

    public String GetRequestBody()
    {
        JSONObject obj = JsonCodec.toJsonObject(logStoreShards);

        JSONObject wrapper = new JSONObject();
        wrapper.put("logstores", obj);

        return wrapper.toString();
    }
}
