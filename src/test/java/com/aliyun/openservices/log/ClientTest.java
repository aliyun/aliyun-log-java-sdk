package com.aliyun.openservices.log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Histogram;
import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.Credentials;
import com.aliyun.openservices.log.functiontest.SlsClientTestData;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.signer.SignVersion;
import com.aliyun.openservices.log.request.ListProjectRequest;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClientTest {
    private static final Credentials credentials = Credentials.load();
    private static final String TEST_ACCESS_KEY_ID = credentials.getAccessKeyId();
    private static final String TEST_ACCESS_KEY = credentials.getAccessKey();

    public static final String TEST_HISTOGRAM_DATA_1 = ("[{'from':100,'to':200,'count':100,'progress':'Complete'},"
            + "{'from':200,'to':300,'count':100,'progress':'Incomplete'}]")
            .replace("'", "\"");

    private static final Client client = new Client("http://mock-sls.aliyun-inc.com",
            TEST_ACCESS_KEY_ID,
            TEST_ACCESS_KEY);

    @Test
    public void testConstruct() {
        new Client("http://mock-sls.aliyun-inc.com", TEST_ACCESS_KEY_ID,
                TEST_ACCESS_KEY);
        new Client("http://mock-sls.aliyun-inc.com", TEST_ACCESS_KEY_ID,
                TEST_ACCESS_KEY, "");
        new Client("https://mock-sls.aliyun-inc.com", TEST_ACCESS_KEY_ID,
                TEST_ACCESS_KEY);
        new Client("mock-sls.aliyun-inc.com", TEST_ACCESS_KEY_ID,
                TEST_ACCESS_KEY);
        new Client("mock-sls.aliyun-inc.com/", TEST_ACCESS_KEY_ID,
                TEST_ACCESS_KEY);
        try {
            new Client("10.1.11.12", TEST_ACCESS_KEY_ID,
                    TEST_ACCESS_KEY);
            fail("Should throw error");
        } catch (IllegalArgumentException e) {
            assertEquals("The ip address is not supported", e.getMessage());
        }
    }

    @Test
    public void testExtractHistograms() {
        JSONArray jObj_1 = JSONArray.parseArray(TEST_HISTOGRAM_DATA_1);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_INCOMPLETE);
        GetHistogramsResponse response_1 = new GetHistogramsResponse(headers);
        response_1.fromJSON(jObj_1);

        // assertEquals(100, meta.mTotalogNum);
        assertEquals(response_1.GetTotalCount(), 200);
        //assertEquals(response_1.IsCompleted(), false);
        ArrayList<Histogram> his_1 = response_1.GetHistograms();
        assertEquals(his_1.size(), 2);
        assertEquals(his_1.get(0).mCount, 100);
        assertEquals(his_1.get(0).mFromTime, 100);
        assertEquals(his_1.get(0).mToTime, 200);
        assertTrue(his_1.get(0).mIsCompleted);
        assertEquals(his_1.get(1).mCount, 100);
        assertEquals(his_1.get(1).mFromTime, 200);
        assertEquals(his_1.get(1).mToTime, 300);
        assertFalse(his_1.get(1).mIsCompleted);
        assertFalse(response_1.IsCompleted());

        JSONArray jObj_2 = JSONArray
                .parseArray(SlsClientTestData.TEST_HISTOGRAM_DATA_2);
        headers = new HashMap<String, String>();
        headers.put(Consts.CONST_X_SLS_PROCESS, Consts.CONST_RESULT_COMPLETE);
        GetHistogramsResponse response_2 = new GetHistogramsResponse(headers);
        response_2.fromJSON(jObj_2);
        // assertEquals(100, meta.mTotalogNum);
        assertEquals(response_2.GetTotalCount(), 300);
        //assertEquals(response_2.IsCompleted(), true);
        ArrayList<Histogram> his_2 = response_2.GetHistograms();
        assertEquals(his_2.size(), 2);
        assertEquals(his_2.get(0).mCount, 100);
        assertEquals(his_2.get(0).mFromTime, 100);
        assertEquals(his_2.get(0).mToTime, 200);
        assertTrue(his_2.get(0).mIsCompleted);
        assertEquals(his_2.get(1).mCount, 200);
        assertEquals(his_2.get(1).mFromTime, 200);
        assertEquals(his_2.get(1).mToTime, 300);
        assertTrue(his_2.get(1).mIsCompleted);
        assertTrue(response_2.IsCompleted());
    }


    @Test(expected = LogException.class)
    public void testErrorCheck() throws LogException {
        JSONObject jObj = JSONObject.parseObject(SlsClientTestData.TEST_ERROR);
        client.ErrorCheck(jObj, "", 200, "");
    }

    @Test
    public void testExtractShards() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("shardID", 0);
        obj.put("status", "readwrite");
        obj.put("inclusiveBeginKey", "e7000000000000000000000000000000");
        obj.put("exclusiveEndKey", "f7000000000000000000000000000000");
        obj.put("createTime", String.valueOf((new Date().getTime()) / 1000));
        JSONArray array = new JSONArray();
        array.add(obj);
        ArrayList<Shard> res = client.ExtractShards(array, "");
        assertEquals("shard num does not match", array.size(), res.size());
        for (int i = 0; i < array.size(); i++) {
            assertEquals("shard does not match", array.getJSONObject(i).getIntValue("shardID"), res.get(i).GetShardId());
        }
    }

    @Test
    public void testGetHostURI() throws Exception {
        Client client = new Client("fake-endpoint",
                TEST_ACCESS_KEY_ID,
                TEST_ACCESS_KEY);
        // Empty is ok?
        client.GetHostURI("");
        client.GetHostURI("123");
        client.GetHostURI("abc");
        client.GetHostURI("a-c");
        client.GetHostURI("1");
        client.GetHostURI("a");
        client.GetHostURI("14");
        try {
            client.GetHostURI("==");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Invalid project: ==");
        }
        try {
            client.GetHostURI("+1");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Invalid project: +1");
        }
        StringBuilder builder = new StringBuilder(130);
        Random r = new Random();
        for (int i = 0; i < 130; i++) {
            int j = 97 + r.nextInt(122 - 97);
            char c = (char) j;
            builder.append(c);
        }
        try {
            client.GetHostURI(builder.toString());
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "Invalid project: " + builder.toString());
        }
        Client client1 = new Client("http://mock-sls.aliyun-inc.com",
                TEST_ACCESS_KEY_ID,
                TEST_ACCESS_KEY);
        client.GetHostURI("abc");
        client.setEndpoint("https://abc////////////");
        URI uri = client.GetHostURI("");
        assertEquals("https://abc", uri.toString());
        client.setEndpoint("mock-sls.aliyun-inc.com "); // trim

        uri = client.GetHostURI("");
        assertEquals("http://mock-sls.aliyun-inc.com", uri.toString());
        List<String> invalidEndpoints = Arrays.asList(
                "http://mock-sls.aliyun-inc.com?abc=def",
                "abc://xxx",
                "http://mock-sls.aliyun-inc.com/?abc=def",
                "http://mock-sls.aliyun-inc.com@hello",
                "////////"
        );
        for (String endpoint : invalidEndpoints) {
            try {
                client1.setEndpoint(endpoint);
                fail("invalid endpoint : " + endpoint);
            } catch (IllegalArgumentException ex) {
                assertEquals(ex.getMessage(), "Invalid endpoint: " + endpoint);
            }
        }
    }

    @Test
    public void testAcdrEndpoint() throws LogException {
        // acdr with default clientConfiguration
        {
            String endpoint = "https://xx-test-acdr-ut-1-intranet.log.aliyuncs.com";
            Client client = new Client(endpoint, "xxx", "yyy");
            ClientConfiguration clientConfiguration = client.getClientConfiguration();
            Assert.assertEquals(clientConfiguration.getRegion(), "xx-test-acdr-ut-1");
            Assert.assertEquals(clientConfiguration.getSignatureVersion(), SignVersion.V4);
        }
        // acdr with custom clientConfiguration
        {
            String endpoint = "xxy-test-acdr-ut-1-intranet.log.aliyuncs.com";
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setMaxErrorRetry(9);
            Client client = new Client(endpoint, "xxx", "yyy", clientConfiguration);
            ClientConfiguration config = client.getClientConfiguration();
            Assert.assertEquals(config.getRegion(), "xxy-test-acdr-ut-1");
            Assert.assertEquals(config.getSignatureVersion(), SignVersion.V4);
            Assert.assertEquals(config.getMaxErrorRetry(), 9);
        }
        // acdr with region and SignatureVersion already set
        {
            String endpoint = "xxy-test-acdr-ut-1-intranet.log.aliyuncs.com";
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setRegion("something");
            clientConfiguration.setSignatureVersion(SignVersion.V4);
            Client client = new Client(endpoint, "xxx", "yyy", clientConfiguration);
            ClientConfiguration config = client.getClientConfiguration();
            Assert.assertEquals(config.getRegion(), "something");
            Assert.assertEquals(config.getSignatureVersion(), SignVersion.V4);
        }
        // not in acdr
        {
            String endpoint = "https://cn-hangzhou-intranet.log.aliyuncs.com";
            Client client = new Client(endpoint, "xxx", "yyy");
            ClientConfiguration clientConfiguration = client.getClientConfiguration();
            Assert.assertNull(clientConfiguration.getRegion());
            Assert.assertEquals(clientConfiguration.getSignatureVersion(), SignVersion.V1);
        }
        // not standard endpoint
        {
            String endpoint = "sls.aliyuncs.com";
            Client client = new Client(endpoint, "xxx", "yyy");
            ClientConfiguration clientConfiguration = client.getClientConfiguration();
            Assert.assertNull(clientConfiguration.getRegion());
            Assert.assertEquals(clientConfiguration.getSignatureVersion(), SignVersion.V1);
        }
    }
}
