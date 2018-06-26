package com.aliyun.openservices.log.functiontest;


import com.aliyun.openservices.log.common.ConsumerGroup;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListConsumerGroupResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConsumerGroupFunctionTest extends FunctionTest {

    private static final String TEST_PROJECT = "project1";
    private static final String TEST_LOGSTORE = "logstore1";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        safeDeleteProject(TEST_PROJECT);
        client.CreateProject(TEST_PROJECT, "testing");
        LogStore logStore = new LogStore();
        logStore.SetLogStoreName(TEST_LOGSTORE);
        logStore.SetShardCount(2);
        logStore.SetTtl(2);
        client.CreateLogStore(TEST_PROJECT, logStore);
    }

    @Test
    public void testListConsumerGroup() throws LogException {
        ListConsumerGroupResponse response = client.ListConsumerGroup(TEST_PROJECT, TEST_LOGSTORE);
        assertEquals(0, response.GetConsumerGroups().size());

        ConsumerGroup consumerGroup = new ConsumerGroup("test1", 100, true);
        client.CreateConsumerGroup(TEST_PROJECT, TEST_LOGSTORE, consumerGroup);

        response = client.ListConsumerGroup(TEST_PROJECT, TEST_LOGSTORE);
        assertEquals(1, response.GetConsumerGroups().size());
        ConsumerGroup group = response.GetConsumerGroups().get(0);
        assertEquals("test1", group.getConsumerGroupName());

        consumerGroup = new ConsumerGroup("test2", 100, true);
        client.CreateConsumerGroup(TEST_PROJECT, TEST_LOGSTORE, consumerGroup);
        response = client.ListConsumerGroup(TEST_PROJECT, TEST_LOGSTORE);
        assertEquals(2, response.GetConsumerGroups().size());

        assertExists(response.GetConsumerGroups(), "test1");
        assertExists(response.GetConsumerGroups(), "test2");
    }

    private static void assertExists(List<ConsumerGroup> groupList, String name) {
        boolean ok = false;
        for (ConsumerGroup item : groupList) {
            if (item.getConsumerGroupName().equals(name)) {
                if (ok) {
                    fail("Expected one consumer group only");
                }
                ok = true;
            }
        }
    }
}
