package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.internal.json.JSONArray;
import com.aliyun.openservices.log.common.ConsumerGroupShardCheckPoint;

import java.util.Map;
import com.aliyun.openservices.log.annotation.InternalApi;

public class GetCheckPointResponse extends Response {

    private static final long serialVersionUID = 4342923949571665580L;

    private ConsumerGroupShardCheckPoint checkpoint;

    @InternalApi
    public GetCheckPointResponse(Map<String, String> headers, JSONArray response) {
        super(headers);
        if (response != null && !response.isEmpty()) {
            checkpoint = new ConsumerGroupShardCheckPoint();
            checkpoint.fromJsonObject(response.getJSONObject(0));
        }
    }

    public ConsumerGroupShardCheckPoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(ConsumerGroupShardCheckPoint checkpoint) {
        this.checkpoint = checkpoint;
    }
}
