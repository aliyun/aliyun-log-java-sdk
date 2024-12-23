package com.aliyun.openservices.log;

import org.junit.Test;

import com.aliyun.openservices.log.response.GetIndexResponse;

public class IndexTest {
    
    @Test
    public void testIndex() throws Exception {
        Client client = new Client("cn-beijing.log.aliyuncs.com",  "", "");
        GetIndexResponse resp = client.GetIndex("tutor-clog", "conan-clog-online");
        System.out.println(resp.GetIndex().ToRequestString());
    }
}
