package com.aliyun.openservices.log.functiontest.metricstore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.MetricsConfig;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.CreateMetricsConfigRequest;
import com.aliyun.openservices.log.request.UpdateMetricsConfigRequest;
import com.aliyun.openservices.log.request.GetMetricsConfigRequest;
import com.aliyun.openservices.log.response.GetMetricsConfigResponse;
import org.junit.Assert;
import org.junit.Test;

public class MetricsConfigTest {

    private String nullValueMetricsConfig = "{\"query_cache_config\":{\"enable\":false},\"parallel_config\":{\"enable\":true,\"mode\":\"auto\",\"time_piece_count\":null,\"parallel_count_per_host\":null,\"total_parallel_count\":null},\"downsampling_config\":{\"base\":{\"create_time\":1685672576932,\"resolution_seconds\":5,\"ttl\":90},\"downsampling\":[{\"create_time\":1685672631875,\"resolution_seconds\":60,\"ttl\":90},{\"create_time\":1685672632535,\"resolution_seconds\":120,\"ttl\":90},{\"create_time\":1685672633334,\"resolution_seconds\":240,\"ttl\":90},{\"create_time\":1685672633961,\"resolution_seconds\":480,\"ttl\":90}]}}";
    private String noPushdownMetricsConfig = "{\"query_cache_config\":{\"enable\":false},\"parallel_config\":{\"enable\":true,\"mode\":\"auto\",\"time_piece_count\":null,\"parallel_count_per_host\":null,\"total_parallel_count\":null},\"downsampling_config\":{\"base\":{\"create_time\":1685672576932,\"resolution_seconds\":5,\"ttl\":90},\"downsampling\":[{\"create_time\":1685672631875,\"resolution_seconds\":60,\"ttl\":90},{\"create_time\":1685672632535,\"resolution_seconds\":120,\"ttl\":90},{\"create_time\":1685672633334,\"resolution_seconds\":240,\"ttl\":90},{\"create_time\":1685672633961,\"resolution_seconds\":480,\"ttl\":90}]}}";
    private String extraMetricsConfig = "{\"query_cache_config\":{\"enable\":false},\"pushdown_config\":{\"enable\":true},\"a\":1,\"parallel_config\":{\"enable\":true,\"mode\":\"auto\",\"time_piece_count\":null,\"parallel_count_per_host\":null,\"total_parallel_count\":null},\"downsampling_config\":{\"base\":{\"create_time\":1685672576932,\"resolution_seconds\":5,\"ttl\":90},\"downsampling\":[{\"create_time\":1685672631875,\"resolution_seconds\":60,\"ttl\":90},{\"create_time\":1685672632535,\"resolution_seconds\":120,\"ttl\":90},{\"create_time\":1685672633334,\"resolution_seconds\":240,\"ttl\":90},{\"create_time\":1685672633961,\"resolution_seconds\":480,\"ttl\":90}]}}";


    @Test
    public void testMetricsConfig(){
        MetricsConfig nullValueConf = JSONObject.parseObject(nullValueMetricsConfig, MetricsConfig.class);
        Assert.assertEquals(nullValueConf.getParallelConfig().getTimePieceCount(), 0);
        MetricsConfig noPushdownConf = JSONObject.parseObject(noPushdownMetricsConfig, MetricsConfig.class);
        Assert.assertNull(noPushdownConf.getPushdownConfig());
        MetricsConfig extraConf = JSONObject.parseObject(extraMetricsConfig, MetricsConfig.class);
        Assert.assertTrue(extraConf != null);
        Assert.assertTrue(extraConf.getPushdownConfig().isEnable());

        final String jsonString = JSON.toJSONString(null);
        System.out.println(jsonString);
    }

    @Test
    public void testRemoteWriteConfig() {
        String conf = "{\"downsampling_config\":null,\"parallel_config\":null,\"query_cache_config\":null,\"pushdown_config\":null,\"remote_write_config\":{\"enable\":true,\"replica_field\":\"\",\"replica_timeout_seconds\":0,\"history_interval\":0,\"future_interval\":0,\"shard_group_strategy_list\":{\"strategies\":[{\"metric_names\":[\".*bucket\"],\"hash_labels\":[\"instance\"],\"shard_group_count\":64,\"priority\":3},{\"metric_names\":[\"up\"],\"hash_labels\":[\"instance\"],\"shard_group_count\":32,\"priority\":4}],\"try_other_shard\":true,\"last_update_time\":1708409523}}}\n";
        MetricsConfig metricsConfig = JSONObject.parseObject(conf, MetricsConfig.class);
        Assert.assertEquals(metricsConfig.getRemoteWriteConfig().getShardGroupStrategyList().getStrategies().size(), 2);
    }

    @Test
    public void testStoreViewRoutingConfig() {
        String conf = "{\"store_view_routing_config\":[{\"metric_names\":[\".*api.*\"],\"project_stores\":[{\"metricstore\":\"prometheus\",\"project\":\"haoqi-sls-metric-test\"}]},{\"metric_names\":[\".*batch.*\"],\"project_stores\":[{\"metricstore\":\"prometheus-1\",\"project\":\"haoqi-sls-metric-test\"}]}]}";
        MetricsConfig metricsConfig = JSONObject.parseObject(conf, MetricsConfig.class);
        final String jsonString = JSONObject.toJSONString(metricsConfig);
        Assert.assertEquals(conf, jsonString);
        System.out.println(jsonString);
    }

    @Test
    public void testCreateRemoteWriteConfig(){
        Client client = new Client(
                "pub-cn-hangzhou-staging-share.log.aliyuncs.com", "xxx", "xxx");

        String conf = "{\"downsampling_config\":null,\"parallel_config\":null,\"query_cache_config\":null,\"pushdown_config\":null,\"remote_write_config\":{\"enable\":true,\"replica_field\":\"\",\"replica_timeout_seconds\":0,\"history_interval\":0,\"future_interval\":0,\"shard_group_strategy_list\":{\"strategies\":[{\"metric_names\":[\".*_bucket\", \"apiserver_.*\"],\"hash_labels\":[],\"shard_group_count\":1,\"priority\":2},{\"metric_names\":[\".*_total\", \".*_count\", \".*_sum\"],\"hash_labels\":[],\"shard_group_count\":8,\"priority\":3},{\"metric_names\":[\".*\"],\"hash_labels\":[],\"shard_group_count\":32,\"priority\":4}],\"try_other_shard\":true,\"last_update_time\":1709192903}}}";

        MetricsConfig metricsConfig = JSONObject.parseObject(conf, MetricsConfig.class);
        final String jsonString = JSONObject.toJSONString(metricsConfig);
        System.out.println(jsonString);
        try{
            client.createMetricsConfig(new CreateMetricsConfigRequest("haoqi-sls-metric-test", "view_test", metricsConfig));
//            client.updateMetricsConfig(new UpdateMetricsConfigRequest("haoqi-sls-metric-test", "view_test", metricsConfig));
            GetMetricsConfigResponse test = client.getMetricsConfig(new GetMetricsConfigRequest("haoqi-sls-metric-test", "view_test"));
            System.out.println(JSONObject.toJSON(test.getMetricsConfig()));
        } catch (LogException e) {
            System.out.println(e);
        }
        //asi-chengdu-classd-unit1-shardgroup
    }

    @Test
    public void testSetTrimLabels(){
        String project = "workspace-default-cms-1654218965343050-cn-hangzhou";
        String metricStore = "aliyun-prom-zhnbih7ixy";
        Client client = new Client("pub-cn-hangzhou-staging-share.log.aliyuncs.com", "xxx", "xxx");

        String conf = "{\"query_cache_config\":null,\"parallel_config\":null,\"downsampling_config\":null,\"pushdown_config\":null,\"remote_write_config\":{\"enable\":true,\"history_interval\":0,\"future_interval\":0,\"replica_field\":\"\",\"replica_timeout_seconds\":0,\"shard_group_strategy_list\":{\"strategies\":null,\"try_other_shard\":false,\"last_update_time\":0},\"trim_same_labels\":true,\"trim_empty_labels\":true},\"store_view_routing_config\":null,\"agg_service_config\":null}";

        MetricsConfig metricsConfig = JSONObject.parseObject(conf, MetricsConfig.class);
        final String jsonString = JSONObject.toJSONString(metricsConfig);
        System.out.println(jsonString);
        try{
//            client.createMetricsConfig(new CreateMetricsConfigRequest(project, metricStore, metricsConfig));
            client.updateMetricsConfig(new UpdateMetricsConfigRequest(project, metricStore, metricsConfig));
//            client.deleteMetricsConfig(new DeleteMetricsConfigRequest(project, metricStore));
            GetMetricsConfigResponse test = client.getMetricsConfig(new GetMetricsConfigRequest(project, metricStore));
            System.out.println(JSONObject.toJSON(test.getMetricsConfig()));
        } catch (LogException e) {
            System.out.println(e);
        }
    }
}
