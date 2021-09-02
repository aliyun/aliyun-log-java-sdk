package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.common.LocalFileConfigInputDetail;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MixLogtailConfigFunctionTest {
    @Test
    public void testFromValidJSON() throws Exception {
        String str = "{\"createTime\":1565005860,\"configName\":\"regex-parse\",\"inputDetail\":{\"delayAlarmBytes\":0,\"filterRegex\":[],\"logBeginRegex\":\".*\",\"timeFormat\":\"\",\"dockerIncludeLabel\":{},\"preserve\":true,\"maxSendRate\":-1,\"discardUnmatch\":false,\"preserveDepth\":1,\"priority\":0,\"mergeType\":\"topic\",\"enableTag\":false,\"advanced\":{\"force_multiconfig\":false},\"localStorage\":true,\"key\":[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\",\"h\",\"i\",\"j\",\"k\"],\"logTimezone\":\"\",\"topicFormat\":\"none\",\"plugin\": {\"processors\": [{\"type\":\"processor_default\", \"detail\":{}},{\"type\":\"processor_default\", \"detail\":{}}]},\"adjustTimezone\":false,\"logPath\":\"/root/test/regex-parse-apache\",\"regex\":\"([0-9\\\\.\\\\-]+)\\\\s([\\\\w\\\\.\\\\-]+)\\\\s([\\\\w\\\\.\\\\-]+)\\\\s(\\\\[[^\\\\[\\\\]]+\\\\]|\\\\-)\\\\s\\\\\\\"(\\\\S+)\\\\s(\\\\S+)\\\\s(\\\\S+)\\\\\\\"\\\\s(\\\\d{3}|\\\\-)\\\\s(\\\\d+|\\\\-)\\\\s\\\\\\\"([^\\\\\\\"]+)\\\\\\\"\\\\s\\\\\\\"([^\\\\\\\"]+)\\\\\\\"\",\"fileEncoding\":\"utf8\",\"tailExisted\":false,\"sendRateExpire\":0,\"dockerExcludeEnv\":{},\"delaySkipBytes\":0,\"filePattern\":\"*.log\",\"maxDepth\":100,\"enableRawLog\":false,\"dockerFile\":false,\"sensitive_keys\":[],\"discardNonUtf8\":false,\"filterKey\":[],\"shardHashKey\":[],\"dockerIncludeEnv\":{},\"logType\":\"common_reg_log\",\"dockerExcludeLabel\":{}},\"inputType\":\"file\",\"outputType\":\"LogService\",\"outputDetail\":{\"logstoreName\":\"logstore1\",\"endpoint\":\"cn-hangzhou-intranet.log.aliyuncs.com\"},\"logSample\":\"192.168.1.9 - - [05/May/2019:19:26:12 +0800] \\\"POST /favicon.ico HTTP/1.1\\\" 200 209 \\\"http://localhost/x0.html\\\" \\\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36\\\"\",\"lastModifyTime\":1565005860}";

        Config cfg = new Config();
        cfg.FromJsonString(str);
        LocalFileConfigInputDetail detail = (LocalFileConfigInputDetail) cfg.GetInputDetail();
        assertFalse(detail.GetPluginDetail().isEmpty());
        assertTrue(cfg.ToJsonString().contains("processors"));
    }

    @Test
    public void testFromInvalidJSON() throws Exception {
        String str = "{\"createTime\":1565005860,\"configName\":\"regex-parse\",\"inputDetail\":{\"delayAlarmBytes\":0,\"filterRegex\":[],\"logBeginRegex\":\".*\",\"timeFormat\":\"\",\"dockerIncludeLabel\":{},\"preserve\":true,\"maxSendRate\":-1,\"discardUnmatch\":false,\"preserveDepth\":1,\"priority\":0,\"mergeType\":\"topic\",\"enableTag\":false,\"advanced\":{\"force_multiconfig\":false},\"localStorage\":true,\"key\":[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\",\"h\",\"i\",\"j\",\"k\"],\"logTimezone\":\"\",\"topicFormat\":\"none\", \"plugin\": {\"processors\": [{\"type\":\"processor_default\", \"detail\":{}},{\"type\":\"processor_default\", \"detail\":{}},invalid]},\"adjustTimezone\":false,\"logPath\":\"/root/test/regex-parse-apache\",\"regex\":\"([0-9\\\\.\\\\-]+)\\\\s([\\\\w\\\\.\\\\-]+)\\\\s([\\\\w\\\\.\\\\-]+)\\\\s(\\\\[[^\\\\[\\\\]]+\\\\]|\\\\-)\\\\s\\\\\\\"(\\\\S+)\\\\s(\\\\S+)\\\\s(\\\\S+)\\\\\\\"\\\\s(\\\\d{3}|\\\\-)\\\\s(\\\\d+|\\\\-)\\\\s\\\\\\\"([^\\\\\\\"]+)\\\\\\\"\\\\s\\\\\\\"([^\\\\\\\"]+)\\\\\\\"\",\"fileEncoding\":\"utf8\",\"tailExisted\":false,\"sendRateExpire\":0,\"dockerExcludeEnv\":{},\"delaySkipBytes\":0,\"filePattern\":\"*.log\",\"maxDepth\":100,\"enableRawLog\":false,\"dockerFile\":false,\"sensitive_keys\":[],\"discardNonUtf8\":false,\"filterKey\":[],\"shardHashKey\":[],\"dockerIncludeEnv\":{},\"logType\":\"common_reg_log\",\"dockerExcludeLabel\":{}},\"inputType\":\"file\",\"outputType\":\"LogService\",\"outputDetail\":{\"logstoreName\":\"logstore1\",\"endpoint\":\"cn-hangzhou-intranet.log.aliyuncs.com\"},\"logSample\":\"192.168.1.9 - - [05/May/2019:19:26:12 +0800] \\\"POST /favicon.ico HTTP/1.1\\\" 200 209 \\\"http://localhost/x0.html\\\" \\\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36\\\"\",\"lastModifyTime\":1565005860}";

        Config cfg = new Config();
        try {
            cfg.FromJsonString(str);
            fail();
        } catch (LogException e) {
            assertTrue(true);
        }
        LocalFileConfigInputDetail detail = (LocalFileConfigInputDetail) cfg.GetInputDetail();
        assertEquals(detail.GetPluginDetail(), "");
        assertFalse(cfg.ToJsonString().contains("plugin"));
    }
}
