package com.aliyun.openservices.log.common;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class AdvancedConfigTest {
    @Test
    public void test() throws LogException {
        Advanced adv = new Advanced();
        adv.setForceMulticonfig(true);
        ArrayList<String> dirArray = new ArrayList<String>();
        dirArray.add("/path/to/dir");
        adv.setDirBlacklist(dirArray);
        adv.setFileNameBlacklist(new ArrayList<String>());
        adv.setFilePathBlacklist(new ArrayList<String>());
        JSONObject others = new JSONObject();
        others.put("tail_size_kb", 100);
        others.put("filter_expression", new JSONObject());
        adv.setOthers(others);

        JSONObject advObj = adv.toJsonObject();
        Assert.assertEquals(advObj.size(), 4);

        Advanced newAdv = Advanced.fromJsonObject(advObj);
        Assert.assertNotSame(adv, newAdv);
        others = newAdv.getOthers();
        Assert.assertEquals(others.size(), 2);
        Assert.assertTrue(others.containsKey("tail_size_kb"));
        Assert.assertEquals(others.getIntValue("tail_size_kb"), 100);
        Assert.assertTrue(others.containsKey("filter_expression"));
        Assert.assertEquals(others.getJSONObject("filter_expression").size(), 0);
    }
}
