package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.*;
import com.aliyun.openservices.log.annotation.InternalApi;

public class ProjectConsumerGroupHeartBeatResponse extends Response {

    private static final long serialVersionUID = 3558359690750583972L;
    private Map<String, List<Integer>> logStoreShards;

    @InternalApi
    public ProjectConsumerGroupHeartBeatResponse(Map<String, String> headers, JSONObject obj) {
        super(headers);
        logStoreShards = new HashMap<String, List<Integer>>();

        JSONObject logStoreShardsMap = obj.getJSONObject("logstores");
        for (String logStore : logStoreShardsMap.keySet()) {
            JSONArray shardJsonArray = logStoreShardsMap.getJSONArray(logStore);
            List<Integer> shardList = new ArrayList<Integer>();
            for (int i = 0; i < shardJsonArray.size(); ++i) {
                shardList.add(shardJsonArray.getIntValue(i));
            }
            this.logStoreShards.put(logStore, shardList);
        }
    }

    /**
     * @return the shards consumer should held in time
     */
    public Map<String, List<Integer>> getLogStoreShards() {
        return logStoreShards;
    }

    public void setLogStoreShards(Map<String, List<Integer>> logStoreShards) {
        this.logStoreShards = logStoreShards;
    }
}
