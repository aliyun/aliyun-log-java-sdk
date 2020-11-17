package com.aliyun.openservices.log.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.Config;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class FastJsonTest {
    @Test
    public void testConfig() throws LogException {
        Config config = new Config();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("configName","1");
        map.put("inputType","2");
//        JSONObject object = new JSONObject(3,true);
//        map.put("inputDetail",object);
//        map.put("outputDetail",object);
        map.put("createTime","123456");
        map.put("lastModifyTime","654321");
        map.put("logSample","666");
        String jsonString = JSONObject.toJSONString(map);
        config.FromJsonString(jsonString);
        Assert.assertEquals(config.GetConfigName(),"1");
    }
    @Test
    public void testECSRoleCredentials() {
        String response = "{\n" +
                "\"AccessKeyId\" : \"STS.J8XXXXXXXXXX4\",\n" +
                "\"AccessKeySecret\" : \"9PjfXXXXXXXXXBf2XAW\",\n" +
                "\"Expiration\" : \"2017-06-09T09:17:19Z\",\n" +
                "\"SecurityToken\" : \"CAIXXXXXXXXXXXwmBkleCTkyI+\",\n" +
                "\"LastUpdated\" : \"2017-06-09T03:17:18Z\",\n" +
                "\"Code\" : \"Success\"\n" +
                "}";
        JSONObject object = JSONObject.parseObject(response);
        assertTrue("Success".equalsIgnoreCase(object.getString("Code")));
        assertTrue("STS.J8XXXXXXXXXX4".equalsIgnoreCase(object.getString("AccessKeyId")));
        assertTrue("9PjfXXXXXXXXXBf2XAW".equalsIgnoreCase(object.getString("AccessKeySecret")));
        assertTrue("2017-06-09T09:17:19Z".equalsIgnoreCase(object.getString("Expiration")));
        assertTrue("CAIXXXXXXXXXXXwmBkleCTkyI+".equalsIgnoreCase(object.getString("SecurityToken")));
    }
}
