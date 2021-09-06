package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.SubStore;
import com.aliyun.openservices.log.common.SubStoreKey;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.CreateSubStoreResponse;
import com.aliyun.openservices.log.response.DeleteSubStoreResponse;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.GetSubStoreResponse;
import com.aliyun.openservices.log.response.GetSubStoreTTLResponse;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListSubStoreResponse;
import com.aliyun.openservices.log.response.UpdateLogStoreResponse;
import com.aliyun.openservices.log.response.UpdateSubStoreTTLResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SubStoreTest extends FunctionTest {
    static int timestamp = getNowTimestamp();
    static String PROJECT = "test-substore-project-" + timestamp;
    static String LOGSTORE1 = "test-substore-logstore-" + timestamp;

    @Before
    public void setUp() {
        safeCreateProject(PROJECT, "SubStoreTest");
        LogStore logStore = new LogStore(LOGSTORE1, 1, 1);
        logStore.setTelemetryType("Metrics");
        createOrUpdateLogStoreNoWait(PROJECT, logStore);
    }

    @After
    public void clearData() {
        safeDeleteLogStore(PROJECT, LOGSTORE1);
        safeDeleteProjectWithoutSleep(PROJECT);
    }

    //this method is not implemented for metric store not Implemented yet!
    @Test
    public void CRUDSubStore() throws LogException {
        createSubStore();
        listSubStore();
        updateSubStore();
        getSubStore();
        updateSubStoreTTL();
        getSubStoreTTL();
        deleteSubStore();
    }

    private void createSubStore() throws LogException {
        SubStore subStore = new SubStore();
        subStore.setName("test_substore_name1");
        subStore.setTtl(15);
        subStore.setTimeIndex(2);
        subStore.setSortedKeyCount(1);
        SubStoreKey subStoreKey1 = new SubStoreKey("__name__", "text");
        SubStoreKey subStoreKey2 = new SubStoreKey("__labels__", "text");
        SubStoreKey subStoreKey3 = new SubStoreKey("__time_nano__", "long");
        SubStoreKey subStoreKey4 = new SubStoreKey("__value__", "double");
        subStore.setKeys(Arrays.asList(subStoreKey1, subStoreKey2, subStoreKey3, subStoreKey4));
        CreateSubStoreResponse createSubStoreResponse = client.createSubStore(PROJECT, LOGSTORE1, subStore);
        assertNotNull(createSubStoreResponse);
    }

    private void listSubStore() throws LogException {
        ListSubStoreResponse listSubStoreResponse = client.listSubStore(PROJECT, LOGSTORE1);
        assertEquals(1, listSubStoreResponse.getSubStoreNames().size());
        assertEquals("test_substore_name1", listSubStoreResponse.getSubStoreNames().get(0));
    }

    private void updateSubStore() {
        SubStore subStore = new SubStore();
        subStore.setName("test_substore_name1");
        subStore.setTtl(15);
        subStore.setTimeIndex(3);
        subStore.setSortedKeyCount(2);
        SubStoreKey subStoreKey1 = new SubStoreKey("__name__", "text");
        SubStoreKey subStoreKey2 = new SubStoreKey("__labels__", "text");
        SubStoreKey subStoreKey3 = new SubStoreKey("__value__", "double");
        SubStoreKey subStoreKey4 = new SubStoreKey("__time_nano__", "long");
        subStore.setKeys(Arrays.asList(subStoreKey1, subStoreKey2, subStoreKey3, subStoreKey4));
        try {
            client.updateSubStore(PROJECT, LOGSTORE1, subStore);
            fail();
        }catch (LogException e){
            assertEquals(e.GetErrorMessage(), "this method is not implemented for metric store not Implemented yet!");
            assertEquals(e.GetErrorCode(), "NotImplemented");
            assertEquals(e.GetHttpCode(), 501);
        }

    }

    private void getSubStore() throws LogException {
        GetSubStoreResponse getSubStoreResponse = client.getSubStore(PROJECT, LOGSTORE1, "test_substore_name1");
        SubStore subStore = getSubStoreResponse.getSubStore();
        assertNotNull(subStore);
        assertEquals("test_substore_name1", subStore.getName());
        assertEquals(15, subStore.getTtl());
        assertEquals(1, subStore.getSortedKeyCount());
        assertEquals(2, subStore.getTimeIndex());
        List<SubStoreKey> keyList = subStore.getKeys();
        assertEquals(4, keyList.size());
    }

    private void deleteSubStore() throws LogException {
        DeleteSubStoreResponse deleteSubStoreResponse = client.deleteSubStore(PROJECT, LOGSTORE1, "test_substore_name1");
        assertNotNull(deleteSubStoreResponse);
    }

    private void updateSubStoreTTL() throws LogException {
        UpdateSubStoreTTLResponse updateSubStoreTTLResponse = client.updateSubStoreTTL(PROJECT, LOGSTORE1, 16);
        assertNotNull(updateSubStoreTTLResponse);
    }

    private void getSubStoreTTL() throws LogException {
        GetSubStoreTTLResponse getSubStoreTTLResponse = client.getSubStoreTTL(PROJECT, LOGSTORE1);
        assertEquals(16, getSubStoreTTLResponse.getTtl());
    }

    //this method is not implemented for metric store not Implemented yet!
    @Test
    public void testCURDLogStoreV2() throws LogException {
        listLogStoreV2();
        updateLogStoreV2();
        getLogStoreV2();
    }

    private void listLogStoreV2() throws LogException {
        ListLogStoresResponse listLogStoreV2Response = client.listLogStores(PROJECT, 0, 100, "", "Metrics");
        assertEquals(1, listLogStoreV2Response.GetLogStores().size());
    }

    private void updateLogStoreV2() throws LogException {
        LogStore logStore = new LogStore(LOGSTORE1, 1, 1);
        logStore.setTelemetryType("Metrics1");
        UpdateLogStoreResponse updateLogStoreV2Response = client.UpdateLogStore(PROJECT, logStore);
        assertNotNull(updateLogStoreV2Response);
    }

    private void getLogStoreV2() throws LogException {
        GetLogStoreResponse getLogStoreResponse = client.GetLogStore(PROJECT, LOGSTORE1);
        assertNotNull(getLogStoreResponse);
        assertEquals("Metrics1", getLogStoreResponse.GetLogStore().getTelemetryType());
    }
}
