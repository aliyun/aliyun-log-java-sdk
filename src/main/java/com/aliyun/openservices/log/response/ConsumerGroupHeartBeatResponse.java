package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsumerGroupHeartBeatResponse extends Response {
    private static final long serialVersionUID = 4941087094480475965L;
    private List<Integer> shards;

    public ConsumerGroupHeartBeatResponse(Map<String, String> headers, List<Integer> shards) {
        super(headers);
        this.shards = shards;
    }

    /**
     * @deprecated Use {@link #ConsumerGroupHeartBeatResponse(Map, List)} instead.
     */
    @Deprecated
    public ConsumerGroupHeartBeatResponse(Map<String, String> headers, ArrayList<Integer> shards) {
        super(headers);
        this.shards = shards;
    }

    /**
     * @return the shards consumer should held in time
     * @deprecated Use {@link #getShards()} instead.
     */
    public ArrayList<Integer> GetShards() {
        return new ArrayList<Integer>(shards);
    }

    /**
     * @deprecated Use {@link #setShards(List)} instead.
     */
    public void SetShards(ArrayList<Integer> shards) {
        this.shards = shards;
    }

    public List<Integer> getShards() {
        return shards;
    }

    public void setShards(List<Integer> shards) {
        this.shards = shards;
    }
}
