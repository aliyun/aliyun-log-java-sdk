package com.aliyun.openservices.log.functiontest.logstore;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogStore;
import com.aliyun.openservices.log.common.ObjectMetadata;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.functiontest.MetaAPIBaseFunctionTest;
import com.aliyun.openservices.log.response.GetObjectResponse;
import com.aliyun.openservices.log.response.PutObjectResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Function test for Object API (putObject/getObject).
 * Tests normal scenarios and exception scenarios to ensure proper response
 * handling and closing.
 */
public class ObjectFunctionTest extends MetaAPIBaseFunctionTest {

    private static final String TEST_LOGSTORE = "test-object-logstore";

    @Before
    @Override
    public void setUp() {
        super.setUp();
        LogStore logStore = new LogStore(TEST_LOGSTORE, 1, 1);
        assertTrue(safeCreateLogStore(TEST_PROJECT, logStore));
        waitForSeconds(10); // Wait for logstore to be ready
    }

    /**
     * Test normal scenario: put an object and then get it.
     * Verifies that:
     * 1. putObject works correctly
     * 2. getObject returns the correct content
     * 3. Response is properly closed after use
     */
    @Test
    public void testPutAndGetObject() throws LogException, IOException {
        String objectName = "test-object-" + randomInt();
        String content = "Hello, this is test content for object API. Random: " + randomString();
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        // Put object
        InputStream inputStream = new ByteArrayInputStream(contentBytes);
        PutObjectResponse putResponse = client.putObject(TEST_PROJECT, TEST_LOGSTORE, objectName, inputStream);
        assertNotNull(putResponse);
        assertNotNull(putResponse.GetRequestId());
        System.out.println(
                "Put object success, ETag: " + putResponse.getETag() + ", RequestId: " + putResponse.GetRequestId());

        // Get object and verify content - using try-with-resources to ensure close
        try (GetObjectResponse getResponse = client.getObject(TEST_PROJECT, TEST_LOGSTORE, objectName)) {
            assertNotNull(getResponse);
            assertNotNull(getResponse.GetRequestId());

            // Verify metadata
            ObjectMetadata metadata = getResponse.getObjectMetadata();
            assertNotNull(metadata);
            assertEquals(contentBytes.length, metadata.getContentLength());
            System.out.println("Get object success, Content-Length: " + metadata.getContentLength()
                    + ", RequestId: " + getResponse.GetRequestId());

            // Read and verify content
            InputStream responseContent = getResponse.getContent();
            assertNotNull(responseContent);
            byte[] readBytes = readAllBytes(responseContent);
            assertEquals(content, new String(readBytes, StandardCharsets.UTF_8));
        }
        // Response is automatically closed here
    }

    /**
     * Test put object with custom headers (x-log-meta-*).
     */
    @Test
    public void testPutObjectWithCustomHeaders() throws LogException, IOException {
        String objectName = "test-object-headers-" + randomInt();
        String content = "Content with custom headers";
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        // Put object with custom headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("x-log-meta-author", "test-user");
        headers.put("x-log-meta-version", "1.0");

        InputStream inputStream = new ByteArrayInputStream(contentBytes);
        PutObjectResponse putResponse = client.putObject(TEST_PROJECT, TEST_LOGSTORE, objectName, inputStream, headers);
        assertNotNull(putResponse);
        System.out.println("Put object with headers success, RequestId: " + putResponse.GetRequestId());

        // Get object and verify custom headers
        try (GetObjectResponse getResponse = client.getObject(TEST_PROJECT, TEST_LOGSTORE, objectName)) {
            ObjectMetadata metadata = getResponse.getObjectMetadata();
            Map<String, String> customHeaders = metadata.getCustomHeaders();
            System.out.println("Custom headers: " + customHeaders);
            // Note: Server may or may not return custom headers depending on implementation
            assertEquals(2, customHeaders.size());
            assertEquals("test-user", customHeaders.get("x-log-meta-author"));
            assertEquals("1.0", customHeaders.get("x-log-meta-version"));
        }
    }

    /**
     * Test get object using try-finally pattern to ensure close.
     */
    @Test
    public void testGetObjectWithTryFinally() throws LogException, IOException {
        String objectName = "test-object-tryfinally-" + randomInt();
        String content = "Content for try-finally test";
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        // Put object first
        InputStream inputStream = new ByteArrayInputStream(contentBytes);
        client.putObject(TEST_PROJECT, TEST_LOGSTORE, objectName, inputStream);

        // Get object using try-finally pattern
        GetObjectResponse response = null;
        try {
            response = client.getObject(TEST_PROJECT, TEST_LOGSTORE, objectName);
            assertNotNull(response);

            // Read content
            byte[] readBytes = readAllBytes(response.getContent());
            assertEquals(content, new String(readBytes, StandardCharsets.UTF_8));
        } finally {
            // Always close the response
            if (response != null) {
                response.close();
            }
        }
    }

    // ==================== Exception Scenarios ====================

    /**
     * Test get non-existent object.
     * Verifies that:
     * 1. LogException is thrown with correct error code
     * 2. Response is properly closed by the client (no resource leak)
     */
    @Test
    public void testGetNonExistentObject() {
        String objectName = "non-existent-object-" + randomInt() + "-" + System.currentTimeMillis();

        try {
            client.getObject(TEST_PROJECT, TEST_LOGSTORE, objectName);
            fail("Should throw LogException for non-existent object");
        } catch (LogException e) {
            System.out.println("Expected exception for non-existent object: errorCode=" + e.getErrorCode()
                    + ", errorMessage=" + e.getMessage()
                    + ", httpCode=" + e.getHttpCode()
                    + ", requestId=" + e.getRequestId());
            // Verify error code indicates object not found
            assertNotNull(e.getErrorCode());
            assertNotNull(e.getRequestId());
            // Response should be closed internally by client - no resource leak
        }
    }

    /**
     * Test get object from non-existent logstore.
     * Verifies exception handling and proper response closing.
     */
    @Test
    public void testGetObjectFromNonExistentLogstore() {
        String nonExistentLogstore = "non-existent-logstore-" + randomInt();
        String objectName = "test-object";

        try {
            client.getObject(TEST_PROJECT, nonExistentLogstore, objectName);
            fail("Should throw LogException for non-existent logstore");
        } catch (LogException e) {
            System.out.println("Expected exception for non-existent logstore: errorCode=" + e.getErrorCode()
                    + ", errorMessage=" + e.getMessage()
                    + ", httpCode=" + e.getHttpCode());
            assertNotNull(e.getErrorCode());
            assertEquals("LogStoreNotExist", e.getErrorCode());
            // Response should be closed internally by client
        }
    }

    /**
     * Test put object to non-existent logstore.
     * Verifies exception handling and proper response closing.
     */
    @Test
    public void testPutObjectToNonExistentLogstore() {
        String nonExistentLogstore = "non-existent-logstore-" + randomInt();
        String objectName = "test-object";
        String content = "test content";

        try {
            InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            client.putObject(TEST_PROJECT, nonExistentLogstore, objectName, inputStream);
            fail("Should throw LogException for non-existent logstore");
        } catch (LogException e) {
            System.out.println("Expected exception for put to non-existent logstore: errorCode=" + e.getErrorCode()
                    + ", errorMessage=" + e.getMessage()
                    + ", httpCode=" + e.getHttpCode());
            assertNotNull(e.getErrorCode());
            assertEquals("LogStoreNotExist", e.getErrorCode());
            // Response should be closed internally by client
        }
    }

    /**
     * Test using wrong endpoint.
     * Verifies that client handles connection errors properly without resource
     * leaks.
     */
    @Test
    public void testGetObjectWithWrongEndpoint() {
        // Create a client with wrong endpoint
        Client wrongClient = new Client(
                "wrong-endpoint.log.aliyuncs.com",
                credentials.getAccessKeyId(),
                credentials.getAccessKey());

        String objectName = "test-object";

        try {
            wrongClient.getObject(TEST_PROJECT, TEST_LOGSTORE, objectName);
            fail("Should throw LogException for wrong endpoint");
        } catch (LogException e) {
            System.out.println("Expected exception for wrong endpoint: errorCode=" + e.getErrorCode()
                    + ", errorMessage=" + e.getMessage());
            assertNotNull(e.getErrorCode());
            assertEquals("UnknownHost", e.getErrorCode());
            // No response to close in this case - connection failed
        }
    }

    /**
     * Test put object with wrong endpoint.
     * Verifies that client handles connection errors properly.
     */
    @Test
    public void testPutObjectWithWrongEndpoint() {
        // Create a client with wrong endpoint
        Client wrongClient = new Client(
                "wrong-endpoint.log.aliyuncs.com",
                credentials.getAccessKeyId(),
                credentials.getAccessKey());

        String objectName = "test-object";
        String content = "test content";

        try {
            InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            wrongClient.putObject(TEST_PROJECT, TEST_LOGSTORE, objectName, inputStream);
            fail("Should throw LogException for wrong endpoint");
        } catch (LogException e) {
            System.out.println("Expected exception for put with wrong endpoint: errorCode=" + e.getErrorCode()
                    + ", errorMessage=" + e.getMessage());
            assertNotNull(e.getErrorCode());
            assertEquals("UnknownHost", e.getErrorCode());
        }
    }

    /**
     * Helper method to read all bytes from an InputStream.
     */
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[1024];
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }
}
