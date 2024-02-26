package com.aliyun.openservices.log.util;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testRemoveNullList() throws Exception{
    
        List<String> list = new ArrayList<String>();
        List<String> ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(0, ret.size());
        
        list.clear();
        list.add("e");
        ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(1, ret.size());

        list.clear();
        list.add("e");
        list.add(null);
        ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(1, ret.size());
        

        list.clear();
        list.add("e");
        list.add(null);
        list.add("e");
        list.add(null);
        list.add("e");
        list.add(null);
        list.add("e");
        list.add(null);
        ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(4, ret.size());

        list.clear();
        list.add(null);
        list.add(null);
        list.add("e");
        list.add(null);
        list.add("e");
        list.add("e");
        list.add(null);
        ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(3, ret.size());

        list.clear();
        list.add(null);
        list.add(null);
        list.add(null);
        list.add("e");
        list.add("e");
        ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(2, ret.size());

        list.clear();
        list.add(null);
        list.add(null);
        list.add(null);
        ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(0, ret.size());

        list.clear();
        list.add("e");
        list.add("e");
        ret = Utils.removeNullItems(list);
        System.out.println(ret);
        assertEquals(2, ret.size());
    }
    @Test
    public void testParseRegion() {
        Assert.assertEquals("cn-hangzhou", Utils.parseRegion("http://cn-hangzhou-intranet.log.aliyuncs.com"));
        Assert.assertEquals("cn-hangzhou", Utils.parseRegion("https://cn-hangzhou.log.aliyuncs.com"));
        Assert.assertEquals("ap-southease-1", Utils.parseRegion("ap-southease-1-intranet.log.aliyuncs.com"));
        Assert.assertEquals("cn-shanghai-corp", Utils.parseRegion("cn-shanghai-corp.sls.aliyuncs.com"));
        Assert.assertNull(Utils.parseRegion("sls.aliyuncs.com"));
    }
}
