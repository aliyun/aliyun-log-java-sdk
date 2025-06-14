package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.Index;
import com.aliyun.openservices.log.common.IndexJsonKey;
import com.aliyun.openservices.log.common.IndexKey;
import com.aliyun.openservices.log.common.IndexKeys;
import com.aliyun.openservices.log.exception.LogException;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SlsIndexJsonFunctionTest extends BaseDataTest {

    @Test
    public void TestUpdateIndex() throws LogException {
        Index index = new Index();
        index.setMaxTextLen(100);
        List<String> whiteBlackList = Arrays.asList("123","456");
        index.setLogReduceWhiteList(whiteBlackList);
        index.setLogReduceBlackList(whiteBlackList);
        index.setLogReduceEnable(true);
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
            client.UpdateIndex(project, logStore.GetLogStoreName(), index);
            fail();
        } catch (LogException e) {
            Assert.assertTrue(e.getMessage().contains("index is not created"));
        }
        client.CreateIndex(project, logStore.GetLogStoreName(), index);
        Index res = client.GetIndex(project, logStore.GetLogStoreName()).GetIndex();
        assertEquals(res.getMaxTextLen(), index.getMaxTextLen());
        assertEquals(res.getLogReduceWhiteList(), whiteBlackList);
        assertEquals(res.getLogReduceBlackList(), whiteBlackList);
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
        
    }


}
