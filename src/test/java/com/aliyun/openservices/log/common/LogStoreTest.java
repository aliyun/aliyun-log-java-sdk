package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Test;

import java.util.Arrays;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class LogStoreTest {

    @Test
    public void testShardingPolicyJsonRoundTrip() throws LogException {
        LogStore logStore = new LogStore("test-logstore", 7, 16);
        logStore.setEnableModify(true);
        logStore.setShardingPolicy(new ShardingPolicy(
                new ShardingPolicy.ShardGroup(Arrays.asList("userId"), 8),
                new ShardingPolicy.ShardHash(Arrays.asList("instanceId", "host"), 4),
                1764659409L));

        JSONObject request = logStore.toRequestJson();
        assertTrue(request.containsKey("shardingPolicy"));
        assertTrue(request.getBooleanValue("enableModify"));
        assertFalse(request.containsKey("createTime"));

        JSONObject policy = request.getJSONObject("shardingPolicy");
        assertEquals(1764659409L, policy.getLongValue("queryActiveTime"));
        assertEquals(8, policy.getJSONObject("shardGroup").getIntValue("groupCount"));
        assertEquals("userId", policy.getJSONObject("shardGroup").getJSONArray("keys").getString(0));
        assertEquals(4, policy.getJSONObject("shardHash").getIntValue("maxHashCount"));
        assertEquals("instanceId", policy.getJSONObject("shardHash").getJSONArray("keys").getString(0));
        assertEquals("host", policy.getJSONObject("shardHash").getJSONArray("keys").getString(1));

        LogStore decoded = new LogStore();
        decoded.fromJsonObject(logStore.toJsonObject());

        assertTrue(decoded.isEnableModify());
        assertNotNull(decoded.getShardingPolicy());
        assertEquals(Long.valueOf(1764659409L), decoded.getShardingPolicy().getQueryActiveTime());
        assertEquals(Arrays.asList("userId"), decoded.getShardingPolicy().getShardGroup().getKeys());
        assertEquals(Integer.valueOf(8), decoded.getShardingPolicy().getShardGroup().getGroupCount());
        assertEquals(Arrays.asList("instanceId", "host"), decoded.getShardingPolicy().getShardHash().getKeys());
        assertEquals(Integer.valueOf(4), decoded.getShardingPolicy().getShardHash().getMaxHashCount());

        LogStore copied = new LogStore(decoded);
        assertTrue(copied.isEnableModify());
        assertNotSame(decoded.getShardingPolicy(), copied.getShardingPolicy());
        assertJsonEquals(decoded.toJsonObject().toString(), copied.toJsonObject().toString());
    }

    @Test
    public void testEnableModifyDefaultsToFalse() throws LogException {
        LogStore logStore = new LogStore("test-logstore", 7, 16);

        assertFalse(logStore.isEnableModify());
        assertFalse(logStore.toRequestJson().getBooleanValue("enableModify"));

        JSONObject dict = logStore.toJsonObject();
        dict.remove("enableModify");

        LogStore decoded = new LogStore();
        decoded.fromJsonObject(dict);

        assertFalse(decoded.isEnableModify());
    }

    @Test
    public void testShardHashDefaultMaxHashCount() throws LogException {
        ShardingPolicy policy = new ShardingPolicy();
        policy.fromJsonString("{\"shardHash\":{\"keys\":[\"instanceId\"]}}");

        assertNotNull(policy.getShardHash());
        assertEquals(Arrays.asList("instanceId"), policy.getShardHash().getKeys());
        assertEquals(Integer.valueOf(2), policy.getShardHash().getMaxHashCount());
        assertEquals(2, policy.toJsonObject().getJSONObject("shardHash").getIntValue("maxHashCount"));
    }
}
