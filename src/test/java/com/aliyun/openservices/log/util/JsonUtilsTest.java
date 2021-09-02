package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {
    public static final String TEST_LIST_LOGSTORE ="{\"count\": 2, \"logstores\":[\"log_store_1\", \"log_store_2\"]}";

    @Test
    public void testReadStringList() {
        JSONObject jObj = JSONObject.parseObject(TEST_LIST_LOGSTORE);
        List<String> logStores = JsonUtils.readStringList(jObj, "logstores");
        assertEquals(2, logStores.size());
        assertEquals("log_store_1", logStores.get(0));
        assertEquals("log_store_2", logStores.get(1));
    }
}
