package com.aliyun.openservices.log.common;

import com.aliyun.openservices.log.internal.json.JSONObject;
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
        JSONObject newOthers = new JSONObject(newAdv.getOthers());
        Assert.assertEquals(newOthers.size(), 2);
        Assert.assertTrue(newOthers.containsKey("tail_size_kb"));
        Assert.assertEquals(newOthers.getIntValue("tail_size_kb"), 100);
        Assert.assertTrue(newOthers.containsKey("filter_expression"));
        Assert.assertEquals(newOthers.getJSONObject("filter_expression").size(), 0);
    }
}
