package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ParameterTest {
    @Test
    public void test() {
        Parameter parameter = new Parameter();
        parameter.setInstanceId("ins");
        parameter.setVpcId("vpc");
        ExternalStore externalStore = new ExternalStore("name-oss", "oss", parameter);
        String jsonString = JSONObject.toJSONString(externalStore);
        System.out.println(jsonString);
        JSONObject object = JSONObject.parseObject(jsonString);
        Set<String> keySet = object.keySet();
        for (String key : keySet) {
            if ("name-oss".equals(object.getString(key))) {
                Assert.assertEquals("externalStoreName", key);
            } else if ("oss".equals(object.getString(key))) {
                Assert.assertEquals("storeType", key);
            } else if ("parameter".equals(object.getString(key))) {
                JSONObject para = object.getJSONObject(key);
                Set<String> innerKeySet = para.keySet();
                for (String innerKey : innerKeySet) {
                    if ("vpc".equals(para.getString(innerKey))) {
                        Assert.assertEquals("vpc-id", innerKey);
                    } else if ("ins".equals(para.getString(innerKey))) {
                        Assert.assertEquals("instance-id", innerKey);
                    }
                }
            }
        }
    }
}
