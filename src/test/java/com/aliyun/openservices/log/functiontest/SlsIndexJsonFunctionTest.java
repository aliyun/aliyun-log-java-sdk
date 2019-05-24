package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexJsonKey;
import com.aliyun.openservices.log.common.IndexKey;
import com.aliyun.openservices.log.common.IndexKeys;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class SlsIndexJsonFunctionTest {

    static final Credentials credentials = Credentials.load();
    static private String accessId = credentials.getAccessKeyId();
    static private String accessKey = credentials.getAccessKey();

    static private String project = "test-lichao-3";
    static private String host = credentials.getEndpoint();
    static private String logStore = "test-java-sdk";

    private int startTime = (int) (new Date().getTime() / 1000);

    static private Client client = new Client(host, accessId, accessKey);

    public SlsIndexJsonFunctionTest() {
    }

    @Test
    public void TestUpdateIndex() {
        Index index = new Index();
        IndexKeys keys = new IndexKeys();
        IndexKey keyContent = new IndexKey();
        keyContent.SetCaseSensitive(false);
        List<String> token = new ArrayList<String>();
        token.add(";");
        keyContent.SetToken(token);
        String keyName = "test";
        keys.AddKey(keyName, keyContent);
        IndexJsonKey indexJsonKey = new IndexJsonKey();
        indexJsonKey.SetToken(token);
        indexJsonKey.SetCaseSensitive(false);
        indexJsonKey.SetDocValue(true);
        indexJsonKey.SetChn(false);
        IndexKeys jsonKeys = new IndexKeys();
        IndexKey indexKey = new IndexKey();
        indexKey.SetToken(token);
        indexKey.SetCaseSensitive(false);
        indexKey.SetDocValue(true);
        indexKey.SetChn(false);
        jsonKeys.AddKey("json1_sub3", indexKey);
        jsonKeys.AddKey("json1_sub4", indexKey);
        indexJsonKey.setJsonKeys(jsonKeys);
        keys.AddKey("json1", indexJsonKey);
        index.SetKeys(keys);
        try {
            client.UpdateIndex(project, logStore, index);
            Index res = client.GetIndex(project, logStore).GetIndex();
            IndexKeys resKeys = res.GetKeys();
            assertEquals(index.GetKeys().GetKeys().size(), resKeys.GetKeys().size());
            assertTrue(resKeys.GetKeys().containsKey("json1"));
            IndexKey resKey = resKeys.GetKeys().get("json1");
            assertEquals(keyContent.GetCaseSensitive(), resKey.GetCaseSensitive());
            assertEquals(keyContent.GetToken().size(), resKey.GetToken().size());
            for (int i = 0; i < keyContent.GetToken().size(); i++) {
                assertEquals(keyContent.GetToken().get(i), resKey.GetToken().get(i));
            }
            assertTrue(resKey instanceof IndexJsonKey);
            IndexKeys indexKeys = ((IndexJsonKey) resKey).getJsonKeys();
            assertTrue(indexKeys.GetKeys().containsKey("json1_sub3"));
            assertTrue(indexKeys.GetKeys().containsKey("json1_sub4"));
        } catch (LogException e) {
            fail(e.GetErrorCode() + ":" + e.GetErrorMessage());
        }
    }


}
