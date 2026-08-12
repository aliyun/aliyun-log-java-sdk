package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JsonCodec;
import com.aliyun.openservices.log.internal.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class ParameterTest {
    @Test
    public void test() {
        Parameter parameter = new Parameter();
        parameter.setInstanceId("ins");
        parameter.setVpcId("vpc");
        ExternalStore externalStore = new ExternalStore("name-oss", "oss", parameter);
        String jsonString = JsonCodec.toJson(externalStore);
        System.out.println(jsonString);
        JSONObject object = JSONObject.parseObject(jsonString);
        Assert.assertEquals("name-oss", object.getString("externalStoreName"));
        Assert.assertEquals("oss", object.getString("storeType"));
        JSONObject para = object.getJSONObject("parameter");
        Assert.assertEquals("vpc", para.getString("vpc-id"));
        Assert.assertEquals("ins", para.getString("instance-id"));
    }
}
