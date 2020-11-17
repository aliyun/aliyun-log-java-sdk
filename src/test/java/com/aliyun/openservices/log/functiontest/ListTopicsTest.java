package com.aliyun.openservices.log.functiontest;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.ListTopicsResponse;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ListTopicsTest extends BaseDataTest {

    @Test
    public void testListTopics() throws LogException {
        enableIndex();
        prepareLogs();
        ListTopicsResponse listTopics = client.ListTopics(project, logStore.GetLogStoreName(), "", 10);
        assertTrue(listTopics.GetTopics().isEmpty());
    }
}
