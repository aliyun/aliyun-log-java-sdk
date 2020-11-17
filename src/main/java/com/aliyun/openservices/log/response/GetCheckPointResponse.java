package com.aliyun.openservices.log.response;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.openservices.log.common.ConsumerGroupShardCheckPoint;

import java.util.Map;

public class GetCheckPointResponse extends Response {

    private static final long serialVersionUID = 4342923949571665580L;

    private ConsumerGroupShardCheckPoint checkpoint;

    public GetCheckPointResponse(Map<String, String> headers, JSONArray response) {
        super(headers);
        if (response != null && !response.isEmpty()) {
            checkpoint = new ConsumerGroupShardCheckPoint();
            checkpoint.Deserialize(response.getJSONObject(0));
        }
    }

    public ConsumerGroupShardCheckPoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(ConsumerGroupShardCheckPoint checkpoint) {
        this.checkpoint = checkpoint;
    }
}