package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListShardResponse;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShardMergeTest extends BaseDataTest {
   
    @Test
    public void testMergeShard() throws LogException {
        ListShardResponse response = client.ListShard(project, logStore.GetLogStoreName());
        List<Shard> shards1 = response.GetShards();
        for (int i = 0; i < shards1.size(); i++) {
            Shard shard = shards1.get(i);
            if (i % 2 == 0) {
                client.MergeShards(project, logStore.GetLogStoreName(), shard.getShardId());
            }
        }

        waitForSeconds(60);

        ListShardResponse response2 = client.ListShard(project, logStore.GetLogStoreName());
        List<Shard> shards2 = response2.GetShards();
        for (Shard shard : shards2) {
            if (shard.getShardId() < shards1.size()) {
                assertEquals("readonly", shard.getStatus());
            } else {
                assertEquals("readwrite", shard.getStatus());
            }
        }
    }
}