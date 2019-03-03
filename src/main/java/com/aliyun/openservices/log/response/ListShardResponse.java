package com.aliyun.openservices.log.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.log.common.Shard;

public class ListShardResponse extends Response {
    private static final long serialVersionUID = 1451751250790494673L;
    private List<Shard> shards;

    public ListShardResponse(Map<String, String> headers, List<Shard> shards) {
        super(headers);
        this.shards = new ArrayList<Shard>(shards);
    }

    /**
     * @deprecated Use {@link #getShards()} instead.
     */
    @Deprecated
    public ArrayList<Shard> GetShards() {
        return new ArrayList<Shard>(shards);
    }

    public List<Shard> getShards() {
        return shards;
    }

    public void setShards(List<Shard> shards) {
        this.shards = shards;
    }
}
