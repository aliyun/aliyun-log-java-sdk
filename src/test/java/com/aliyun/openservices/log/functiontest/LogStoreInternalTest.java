package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.common.InternalLogStore;
import com.aliyun.openservices.log.exception.LogException;
import org.junit.Test;

import java.util.ArrayList;

public class LogStoreInternalTest extends MetaAPIBaseFunctionTest {
    private static final String TEST_LOGSTORE = "test-store-" + getNowTimestamp();

    @Test
    public void testCreateAndUpdate() {
        //Unauthorized
        //internal api only support admin account invoke
        try {
            InternalLogStore internalLogStore = getInternalLogStore();
            client.CreateLogStoreInternal(TEST_PROJECT, internalLogStore);
        } catch (LogException e) {
            System.err.println(e.GetErrorCode());
            System.err.println(e.GetErrorMessage());
        }

        try {
            InternalLogStore internalLogStore = getInternalLogStore();
            internalLogStore.setAllFree(false);
            client.UpdateLogStoreInternal(TEST_PROJECT, internalLogStore);
        } catch (LogException e) {
            System.err.println(e.GetErrorCode());
            System.err.println(e.GetErrorMessage());
        }
    }

    private InternalLogStore getInternalLogStore() {
        InternalLogStore internalLogStore = new InternalLogStore();
        internalLogStore.SetLogStoreName(TEST_LOGSTORE);
        internalLogStore.SetTtl(10);
        internalLogStore.SetShardCount(1);
        ArrayList<String> operatingAccounts = new ArrayList<String>();
        operatingAccounts.add("123");
        internalLogStore.setOperatingAccount(operatingAccounts);
        ArrayList<String> restrictedAction = new ArrayList<String>();
        restrictedAction.add("567");
        internalLogStore.setRestrictedAction(restrictedAction);
        internalLogStore.setPaidAccount("paidAccount");
        internalLogStore.setAllFree(true);
        internalLogStore.setReadCount(100L);
        internalLogStore.setWriteCount(100L);
        internalLogStore.setInflowSize(1000L);
        internalLogStore.setOutflowSize(1000L);
        internalLogStore.setIndexSize(10L);
        internalLogStore.setShardSize(20L);
        internalLogStore.setFreeTtl(7L);
        internalLogStore.setProductType("testProduct");
        return internalLogStore;
    }
}
