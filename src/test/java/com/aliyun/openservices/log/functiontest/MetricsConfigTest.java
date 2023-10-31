package com.aliyun.openservices.log.functiontest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.MetricsConfig;
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
}
