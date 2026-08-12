package com.aliyun.openservices.log.internal.json;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.aliyun.openservices.log.common.MetricDownSamplingConfig;
import com.aliyun.openservices.log.common.MetricParallelConfig;
import com.aliyun.openservices.log.common.MetricPushdownConfig;
import com.aliyun.openservices.log.common.MetricQueryCacheConfig;
import com.aliyun.openservices.log.common.MetricRemoteWriteConfig;
import com.aliyun.openservices.log.common.MetricStoreViewRoutingConfig;
import com.aliyun.openservices.log.common.MetricsConfig;
import com.aliyun.openservices.log.response.GetMetricsConfigResponse;
import com.aliyun.openservices.log.response.ListMetricsConfigResponse;
import com.aliyun.openservices.log.util.JsonUtils;

import static com.aliyun.openservices.log.internal.json.JsonAsserts.assertJsonEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MetricsConfigSerializationTest {

    private static final String DETAIL = "{\"query_cache_config\":{\"enable\":false},"
            + "\"remote_write_config\":{\"enable\":true,\"history_interval\":3600,"
            + "\"future_interval\":60,\"replica_field\":\"replica-中文<>&=\","
            + "\"replica_timeout_seconds\":30,\"trim_same_labels\":true,"
            + "\"trim_empty_labels\":false,\"utf8_write_enable\":true}}";

    @Test
    public void serializesProtocolFieldNamesAndExplicitFalseValues() throws Exception {
        assertJsonEquals(DETAIL, metricsConfig().toJsonString());
        assertJsonEquals(DETAIL, JsonUtils.serialize(metricsConfig()));
    }

    @Test
    public void publicStringRoundTripPreservesCompleteMetricsWireContract() throws Exception {
        String expected = "{\"query_cache_config\":{\"enable\":false},"
                + "\"parallel_config\":{\"enable\":false,\"mode\":\"parallel-中文\\\"\\\\path\","
                + "\"time_piece_interval\":0,\"time_piece_count\":2,"
                + "\"parallel_count_per_host\":0,\"total_parallel_count\":4},"
                + "\"downsampling_config\":{\"base\":{\"create_time\":0,\"ttl\":0,"
                + "\"resolution_seconds\":0},\"downsampling\":[{\"create_time\":123,"
                + "\"ttl\":7,\"resolution_seconds\":60}]},"
                + "\"pushdown_config\":{\"enable\":false},"
                + "\"remote_write_config\":{\"enable\":true,\"history_interval\":3600,"
                + "\"future_interval\":60,\"replica_field\":\"replica-中文<>&=\\\"\\\\path\","
                + "\"replica_timeout_seconds\":30,\"shard_group_strategy_list\":{"
                + "\"strategies\":[{\"metric_names\":[\"cpu\",\"内存\"],\"hash_labels\":[],"
                + "\"shard_group_count\":0,\"priority\":1}],\"try_other_shard\":false,"
                + "\"last_update_time\":0},\"trim_same_labels\":true,"
                + "\"trim_empty_labels\":false,\"utf8_write_enable\":true},"
                + "\"store_view_routing_config\":[{\"metric_names\":[\"cpu\",\"内存\"],"
                + "\"project_stores\":[{\"project\":\"project-中文\","
                + "\"metricstore\":\"store-\\\"quoted\\\"-\\\\path\"}]}]}";

        MetricsConfig original = completeMetricsConfig();
        String json = original.toJsonString();
        assertJsonEquals(expected, json);

        MetricsConfig decoded = new MetricsConfig();
        decoded.fromJsonString(json);
        assertFalse(decoded.getQueryCacheConfig().isEnable());
        assertEquals("parallel-中文\"\\path", decoded.getParallelConfig().getMode());
        assertEquals(0, decoded.getParallelConfig().getTimePieceInterval());
        assertFalse(decoded.getPushdownConfig().isEnable());
        assertEquals(0, decoded.getDownSamplingConfig().getBase().getCreateTime());
        assertEquals(60, decoded.getDownSamplingConfig().getDownsampling()
                .get(0).getResolutionSeconds());
        assertEquals("replica-中文<>&=\"\\path", decoded.getRemoteWriteConfig().getReplicaField());
        assertFalse(decoded.getRemoteWriteConfig().getShardGroupStrategyList().isTryOtherShard());
        assertEquals(Collections.<String>emptyList(), decoded.getRemoteWriteConfig()
                .getShardGroupStrategyList().getStrategies().get(0).getHashLabels());
        assertEquals("project-中文", decoded.getStoreViewRoutingConfigs().get(0)
                .getProjectStores().get(0).getProjectName());
        assertJsonEquals(json, decoded.toJsonString());
    }

    @Test
    public void publicMetricsStringApisPreserveNullsAndEmptyLists() throws Exception {
        MetricsConfig config = new MetricsConfig(new MetricQueryCacheConfig());
        config.fromJsonString("{}");
        assertNull(config.getQueryCacheConfig());
        assertNull(config.getParallelConfig());
        assertNull(config.getDownSamplingConfig());
        assertNull(config.getPushdownConfig());
        assertNull(config.getRemoteWriteConfig());
        assertNull(config.getStoreViewRoutingConfigs());
        assertJsonEquals("{}", config.toJsonString());

        config.setStoreViewRoutingConfigs(
                Collections.<MetricStoreViewRoutingConfig>emptyList());
        assertJsonEquals("{\"store_view_routing_config\":[]}", config.toJsonString());
    }

    @Test
    public void downSamplingSupportsPublicStringRoundTrip() throws Exception {
        MetricDownSamplingConfig config = downSamplingConfig();
        String json = config.toJsonString();
        assertJsonEquals("{\"base\":{\"create_time\":0,\"ttl\":0,"
                        + "\"resolution_seconds\":0},\"downsampling\":[{\"create_time\":123,"
                        + "\"ttl\":7,\"resolution_seconds\":60}]}",
                json);

        MetricDownSamplingConfig decoded = new MetricDownSamplingConfig();
        decoded.fromJsonString(json);
        assertEquals(0, decoded.getBase().getCreateTime());
        assertEquals(0, decoded.getBase().getTtl());
        assertEquals(0, decoded.getBase().getResolutionSeconds());
        assertEquals(1, decoded.getDownsampling().size());
        assertEquals(123, decoded.getDownsampling().get(0).getCreateTime());
        assertEquals(7, decoded.getDownsampling().get(0).getTtl());
        assertEquals(60, decoded.getDownsampling().get(0).getResolutionSeconds());
        assertJsonEquals(json, decoded.toJsonString());

        decoded.setBase(null);
        decoded.setDownsampling(Collections.<MetricDownSamplingConfig.MetricDownSamplingStatus>emptyList());
        assertJsonEquals("{\"downsampling\":[]}", decoded.toJsonString());
    }

    @Test
    public void getResponseDeserializesNestedMetricsConfigJson() throws Exception {
        GetMetricsConfigResponse response = new GetMetricsConfigResponse(headers());
        JSONObject body = new JSONObject();
        body.put("metricsConfigDetail", DETAIL);

        response.fromJsonObject(body);

        assertMetricsConfig(response.getMetricsConfig());
    }

    @Test
    public void listResponseDeserializesNestedMetricsConfigJson() {
        ListMetricsConfigResponse response = new ListMetricsConfigResponse(headers());
        JSONObject item = new JSONObject();
        item.put("metricStore", "metric-a");
        item.put("metricsConfigDetail", DETAIL);
        JSONObject body = new JSONObject();
        JSONArray items = new JSONArray();
        items.add(item);
        body.put("metricsConfig", items);

        response.fromJsonObject(body);

        assertEquals(1, response.getMetricsConfigList().size());
        assertEquals("metric-a", response.getMetricsConfigList().get(0).getMetricStore());
        assertMetricsConfig(response.getMetricsConfigList().get(0).getMetricsConfig());
    }

    private static MetricsConfig metricsConfig() {
        MetricQueryCacheConfig queryCache = new MetricQueryCacheConfig();
        queryCache.setEnable(false);
        MetricRemoteWriteConfig remoteWrite = new MetricRemoteWriteConfig();
        remoteWrite.setEnable(true);
        remoteWrite.setHistoryInterval(3600);
        remoteWrite.setFutureInterval(60);
        remoteWrite.setReplicaField("replica-中文<>&=");
        remoteWrite.setReplicaTimeoutSeconds(30);
        remoteWrite.setTrimSameLabels(true);
        remoteWrite.setTrimEmptyLabels(false);
        remoteWrite.setUtf8WriteEnable(true);
        MetricsConfig config = new MetricsConfig(queryCache);
        config.setRemoteWriteConfig(remoteWrite);
        return config;
    }

    private static MetricsConfig completeMetricsConfig() {
        MetricQueryCacheConfig queryCache = new MetricQueryCacheConfig();
        queryCache.setEnable(false);

        MetricParallelConfig parallel = new MetricParallelConfig();
        parallel.setEnable(false);
        parallel.setMode("parallel-中文\"\\path");
        parallel.setTimePieceInterval(0);
        parallel.setTimePieceCount(2);
        parallel.setParallelCountPerHost(0);
        parallel.setTotalParallelCount(4);

        MetricPushdownConfig pushdown = new MetricPushdownConfig();
        pushdown.setEnable(false);

        MetricRemoteWriteConfig remoteWrite = new MetricRemoteWriteConfig();
        remoteWrite.setEnable(true);
        remoteWrite.setHistoryInterval(3600);
        remoteWrite.setFutureInterval(60);
        remoteWrite.setReplicaField("replica-中文<>&=\"\\path");
        remoteWrite.setReplicaTimeoutSeconds(30);
        remoteWrite.setTrimSameLabels(true);
        remoteWrite.setTrimEmptyLabels(false);
        remoteWrite.setUtf8WriteEnable(true);

        MetricRemoteWriteConfig.ShardGroupStrategy strategy =
                new MetricRemoteWriteConfig.ShardGroupStrategy();
        strategy.setMetricNames(Arrays.asList("cpu", "内存"));
        strategy.setHashLabels(Collections.<String>emptyList());
        strategy.setShardGroupCount(0);
        strategy.setPriority(1);
        MetricRemoteWriteConfig.ShardGroupStrategyList strategyList =
                new MetricRemoteWriteConfig.ShardGroupStrategyList();
        strategyList.setStrategies(Collections.singletonList(strategy));
        strategyList.setTryOtherShard(false);
        strategyList.setLastUpdateTime(0);
        remoteWrite.setShardGroupStrategyList(strategyList);

        MetricStoreViewRoutingConfig.ProjectStore projectStore =
                new MetricStoreViewRoutingConfig.ProjectStore(
                        "project-中文", "store-\"quoted\"-\\path");
        MetricStoreViewRoutingConfig routing = new MetricStoreViewRoutingConfig(
                Arrays.asList("cpu", "内存"), Collections.singletonList(projectStore));

        return new MetricsConfig(queryCache, parallel, downSamplingConfig(), pushdown,
                remoteWrite, Collections.singletonList(routing));
    }

    private static MetricDownSamplingConfig downSamplingConfig() {
        MetricDownSamplingConfig.MetricDownSamplingStatus base =
                new MetricDownSamplingConfig.MetricDownSamplingStatus();
        base.setCreateTime(0);
        base.setTtl(0);
        base.setResolutionSeconds(0);
        MetricDownSamplingConfig.MetricDownSamplingStatus downsampled =
                new MetricDownSamplingConfig.MetricDownSamplingStatus();
        downsampled.setCreateTime(123);
        downsampled.setTtl(7);
        downsampled.setResolutionSeconds(60);
        MetricDownSamplingConfig config = new MetricDownSamplingConfig();
        config.setBase(base);
        config.setDownsampling(Collections.singletonList(downsampled));
        return config;
    }

    private static void assertMetricsConfig(MetricsConfig config) {
        assertFalse(config.getQueryCacheConfig().isEnable());
        MetricRemoteWriteConfig remoteWrite = config.getRemoteWriteConfig();
        assertTrue(remoteWrite.isEnable());
        assertEquals(3600, remoteWrite.getHistoryInterval());
        assertEquals(60, remoteWrite.getFutureInterval());
        assertEquals("replica-中文<>&=", remoteWrite.getReplicaField());
        assertEquals(30, remoteWrite.getReplicaTimeoutSeconds());
        assertTrue(remoteWrite.isTrimSameLabels());
        assertFalse(remoteWrite.isTrimEmptyLabels());
        assertTrue(remoteWrite.isUtf8WriteEnable());
    }

    private static Map<String, String> headers() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-log-requestid", "test-request-id");
        return headers;
    }
}
