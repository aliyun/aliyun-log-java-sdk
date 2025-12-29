package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.ObjectMetadata;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetObjectResponse;
import com.aliyun.openservices.log.response.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample code to demonstrate Object API usage.
 * <p>
 * This example shows how to use putObject and getObject methods.
 */
public class ObjectSample {
    private Client client;

    ObjectSample() {
        String endpoint = "";
        String accessKeyID = "test_accessKeyId";
        String accessKeySecret = "test_accessKey";


        // create a client
        this.client = new Client(endpoint, accessKeyID, accessKeySecret);
    }

    public static void main(String[] args) {
        ObjectSample sample = new ObjectSample();
        String project = "test-project";
        String logStore = "test-logstore";
        String objectName = "test_object_1";

        try {
            // Example 1: Put a simple text object
            sample.putSimpleObject(project, logStore, objectName);

            // Example 2: Put an object with custom headers
            sample.putObjectWithHeaders(project, logStore, "test_object_2");

            // Example 3: Get an object
            sample.getObject(project, logStore, objectName);

            // Example 4: Get an object using try-with-resources
            sample.getObjectWithTryWithResources(project, logStore, objectName);
        } catch (LogException e) {
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Error requestId: " + e.getRequestId());
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }
    }

    /**
     * Example 1: Put a simple text object
     */
    public void putSimpleObject(String project, String logStore, String objectName) throws LogException {
        System.out.println("============================================================");
        System.out.println("Sample: Put Simple Object");
        System.out.println("============================================================");

        String content = "Hello, this is test content";
        byte[] contentBytes = content.getBytes();
        InputStream contentStream = new ByteArrayInputStream(contentBytes);

        PutObjectResponse response = client.putObject(project, logStore, objectName, contentStream);
        System.out.println("Put object success!");
        System.out.println("ETag: " + response.getETag());
        System.out.println("RequestId: " + response.GetRequestId());
    }

    /**
     * Example 2: Put an object with custom headers
     */
    public void putObjectWithHeaders(String project, String logStore, String objectName) throws LogException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Put Object with Custom Headers");
        System.out.println("============================================================");

        String content = "Content with metadata";
        byte[] contentBytes = content.getBytes();
        InputStream contentStream = new ByteArrayInputStream(contentBytes);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/plain");
        headers.put("x-log-meta-author", "test_user");
        headers.put("x-log-meta-version", "1.0");

        PutObjectResponse response = client.putObject(project, logStore, objectName, contentStream, headers);
        System.out.println("Put object with headers success!");
        System.out.println("ETag: " + response.getETag());
        System.out.println("RequestId: " + response.GetRequestId());
    }

    /**
     * Example 3: Get an object
     */
    public void getObject(String project, String logStore, String objectName) throws LogException, IOException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Get Object");
        System.out.println("============================================================");

        GetObjectResponse response = null;
        try {
            response = client.getObject(project, logStore, objectName);

            System.out.println("Get object success!");
            ObjectMetadata metadata = response.getObjectMetadata();
            System.out.println("ETag: " + metadata.getETag());
            System.out.println("Last Modified: " + metadata.getLastModified());
            System.out.println("Content Type: " + metadata.getContentType());
            System.out.println("Content Length: " + metadata.getContentLength());
            System.out.println("RequestId: " + response.GetRequestId());

            // Access user metadata (x-log-meta-*)
            Map<String, String> userMetadata = metadata.getCustomHeaders();
            if (!userMetadata.isEmpty()) {
                System.out.println("User Metadata:");
                for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                }
            }

            // Read the content
            InputStream content = response.getContent();
            if (content != null) {
                byte[] buffer = new byte[1024];
                StringBuilder contentBuilder = new StringBuilder();
                int bytesRead;
                while ((bytesRead = content.read(buffer)) != -1) {
                    contentBuilder.append(new String(buffer, 0, bytesRead));
                }
                System.out.println("Content: " + contentBuilder.toString());
            } else {
                System.out.println("Content: (empty)");
            }
        } finally {
            // Always close the response when done
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Example 4: Get an object using try-with-resources
     */
    public void getObjectWithTryWithResources(String project, String logStore, String objectName)
            throws LogException, IOException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Get Object with try-with-resources");
        System.out.println("============================================================");

        try (GetObjectResponse response = client.getObject(project, logStore, objectName)) {
            System.out.println("Get object success!");
            ObjectMetadata metadata = response.getObjectMetadata();
            System.out.println("ETag: " + metadata.getETag());
            System.out.println("Last Modified: " + metadata.getLastModified());
            System.out.println("Content Type: " + metadata.getContentType());

            // Read the content
            InputStream content = response.getContent();
            if (content != null) {
                byte[] buffer = new byte[1024];
                StringBuilder contentBuilder = new StringBuilder();
                int bytesRead;
                while ((bytesRead = content.read(buffer)) != -1) {
                    contentBuilder.append(new String(buffer, 0, bytesRead));
                }
                System.out.println("Content: " + contentBuilder.toString());
            }
        }
        // Response is automatically closed here
    }
}

