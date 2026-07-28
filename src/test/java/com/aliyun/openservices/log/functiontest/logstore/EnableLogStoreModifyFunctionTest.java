package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetLogStoreResponse;
import com.aliyun.openservices.log.response.VoidResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Functional tests for enabling Store Modify on an existing logstore.
 *
 * <p>The target region must have the EnableLogStoreModify API enabled.</p>
 */
public class EnableLogStoreModifyFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = makeProjectName();
    private static final int MAX_WAIT_SECONDS = 180;
    private static final int POLL_INTERVAL_SECONDS = 5;

    @Before
    public void setUp() {
        safeCreateProject(TEST_PROJECT, "SDK EnableLogStoreModify function test");
    }

    @After
    public void tearDown() {
        safeDeleteProjectWithoutSleep(TEST_PROJECT);
    }

    @Test
    public void testEnableLogStoreModify() throws Exception {
        String logStoreName = "enable-modify-" + getNowTimestamp() + "-" + randomBetween(0, 10000);
        LogStore logStore = new LogStore(logStoreName, 1, 1);
        client.CreateLogStore(TEST_PROJECT, logStore);

        GetLogStoreResponse initialResponse = client.GetLogStore(TEST_PROJECT, logStoreName);
        assertFalse(initialResponse.GetLogStore().isEnableModify());

        VoidResponse firstResponse = client.enableLogStoreModify(TEST_PROJECT, logStoreName);
        assertFalse(firstResponse.GetRequestId().isEmpty());

        // Repeated calls must be idempotent while conversion is active or after it completes.
        VoidResponse repeatedResponse = client.enableLogStoreModify(TEST_PROJECT, logStoreName);
        assertFalse(repeatedResponse.GetRequestId().isEmpty());

        waitUntilEnableModifyCompleted(logStoreName);
    }

    private void waitUntilEnableModifyCompleted(String logStoreName) throws LogException {
        int maxAttempts = MAX_WAIT_SECONDS / POLL_INTERVAL_SECONDS;
        for (int i = 0; i <= maxAttempts; i++) {
            GetLogStoreResponse response = client.GetLogStore(TEST_PROJECT, logStoreName);
            if (response.GetLogStore().isEnableModify()) {
                return;
            }
            if (i < maxAttempts) {
                waitForSeconds(POLL_INTERVAL_SECONDS);
            }
        }
        fail("Timed out waiting for Store Modify to be enabled for logstore " + logStoreName);
    }
}
