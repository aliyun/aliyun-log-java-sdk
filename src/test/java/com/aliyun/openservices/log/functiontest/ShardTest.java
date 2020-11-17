package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.SplitShardRequest;
import com.aliyun.openservices.log.response.ListShardResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShardTest extends BaseDataTest {
    @Test
    public void testSplitShard() throws LogException {
        ListShardResponse response1 = client.ListShard(project, logStore.GetLogStoreName());
        List<Shard> shards1 = response1.GetShards();
        for (Shard shard : shards1) {
            SplitShardRequest splitShardRequest = new SplitShardRequest(project, logStore.GetLogStoreName(), shard.GetShardId(), 2);
            client.SplitShard(splitShardRequest);
        }

        waitForSeconds(60);
        ListShardResponse response2 = client.ListShard(project, logStore.GetLogStoreName());
        List<Shard> shards2 = response2.GetShards();
        List<Integer> integers = new ArrayList<Integer>();
        for (Shard shard : shards1) {
            integers.add(shard.GetShardId());
        }
        for (Shard shard : shards2) {
            if (shard.GetShardId() < shards1.size()) {
                assertEquals("readonly", shard.getStatus());
            } else {
                assertEquals("readwrite", shard.getStatus());
            }
        }
    }

    @Test
    public void testMergeShard() throws LogException {
        ListShardResponse response = client.ListShard(project, logStore.GetLogStoreName());
        List<Shard> shards1 = response.GetShards();
        for (int i = 0; i < shards1.size(); i++) {
            if (i % 2 == 0) {
                client.MergeShards(project, logStore.GetLogStoreName(), shards1.get(i).GetShardId());
            }
        }

        waitForSeconds(60);

        ListShardResponse response2 = client.ListShard(project, logStore.GetLogStoreName());
        List<Shard> shards2 = response2.GetShards();
        for (Shard shard : shards2) {
            if (shard.GetShardId() < shards1.size()) {
                assertEquals("readonly", shard.getStatus());
            } else {
                assertEquals("readwrite", shard.getStatus());
            }
        }
    }
}
