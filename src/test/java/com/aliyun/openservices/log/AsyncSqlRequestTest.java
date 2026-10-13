package com.aliyun.openservices.log;

import com.aliyun.openservices.log.request.DeleteAsyncSqlRequest;
import com.aliyun.openservices.log.request.GetAsyncSqlRequest;
import org.junit.Test;

import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AsyncSqlRequestTest {

    @Test
    public void testSerializationVersionMatchesExistingRequests() {
        assertEquals(-7007014235272158263L,
                ObjectStreamClass.lookup(GetAsyncSqlRequest.class).getSerialVersionUID());
        assertEquals(5259113902727799917L,
                ObjectStreamClass.lookup(DeleteAsyncSqlRequest.class).getSerialVersionUID());
    }

    @Test
    public void testGetWithoutLogstorePreservesExistingRequest() {
        GetAsyncSqlRequest request = new GetAsyncSqlRequest("project", "query-id", 10, 20);

        assertNull(request.getLogstore());
        assertGetRequest(request, null);
    }

    @Test
    public void testGetWithLogstore() {
        GetAsyncSqlRequest request = new GetAsyncSqlRequest("project", "logstore", "query-id", 10, 20);

        assertEquals("logstore", request.getLogstore());
        assertGetRequest(request, "logstore");
    }

    @Test
    public void testGetWithNullOrEmptyLogstoreOmitsParameter() {
        assertGetRequest(new GetAsyncSqlRequest("project", null, "query-id", 10, 20), null);
        assertGetRequest(new GetAsyncSqlRequest("project", "", "query-id", 10, 20), null);
    }

    @Test
    public void testDeleteWithoutLogstorePreservesExistingRequest() {
        assertDeleteRequest(new DeleteAsyncSqlRequest("project", "query-id"), null);
    }

    @Test
    public void testDeleteWithLogstore() {
        assertDeleteRequest(new DeleteAsyncSqlRequest("project", "logstore", "query-id"), "logstore");
    }

    @Test
    public void testDeleteWithNullOrEmptyLogstoreOmitsParameter() {
        assertDeleteRequest(new DeleteAsyncSqlRequest("project", null, "query-id"), null);
        assertDeleteRequest(new DeleteAsyncSqlRequest("project", "", "query-id"), null);
    }

    @Test
    public void testDeletePreservesInheritedParameterAccess() {
        DeleteAsyncSqlRequest[] requests = {
                new DeleteAsyncSqlRequest("project", "query-id"),
                new DeleteAsyncSqlRequest("project", "logstore", "query-id")
        };
        for (DeleteAsyncSqlRequest request : requests) {
            Map<String, String> params = request.GetAllParams();
            request.SetParam("custom", "value");
            assertEquals("value", request.GetParam("custom"));
            assertEquals("value", request.GetAllParams().get("custom"));
            request.SetParam("logstore", "another-logstore");
            assertEquals("another-logstore", request.GetParam("logstore"));
            assertEquals("another-logstore", request.GetAllParams().get("logstore"));
            assertSame(params, request.GetAllParams());
        }
    }

    private void assertGetRequest(GetAsyncSqlRequest request, String logstore) {
        assertEquals("project", request.GetProject());
        assertEquals("query-id", request.getQueryId());
        assertEquals(10, request.getOffset());
        assertEquals(20, request.getLines());
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("offset", "10");
        expected.put("line", "20");
        if (logstore != null) {
            expected.put("logstore", logstore);
        }
        assertEquals(expected, request.GetAllParams());
    }

    private void assertDeleteRequest(DeleteAsyncSqlRequest request, String logstore) {
        assertEquals("project", request.GetProject());
        assertEquals("query-id", request.getQueryId());
        Map<String, String> expected = new HashMap<String, String>();
        if (logstore != null) {
            expected.put("logstore", logstore);
        }
        assertEquals(expected, request.GetAllParams());
    }
}
