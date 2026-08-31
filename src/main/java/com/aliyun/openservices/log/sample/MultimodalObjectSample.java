package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.MultimodalStatus;
import com.aliyun.openservices.log.common.ObjectMetadata;
import com.aliyun.openservices.log.common.ObjectSummary;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GeneratePresignedUrlRequest;
import com.aliyun.openservices.log.request.GetLogStoreMultimodalConfigurationRequest;
import com.aliyun.openservices.log.request.ListObjectsRequest;
import com.aliyun.openservices.log.request.PutLogStoreMultimodalConfigurationRequest;
import com.aliyun.openservices.log.response.GeneratePresignedUrlResponse;
import com.aliyun.openservices.log.response.GetLogStoreMultimodalConfigurationResponse;
import com.aliyun.openservices.log.response.GetObjectResponse;
import com.aliyun.openservices.log.response.ListObjectsResponse;
import com.aliyun.openservices.log.response.PutObjectResponse;
import com.aliyun.openservices.log.response.VoidResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Sample code to demonstrate the whole multimodal object flow:
 * <p>
 * 1. putLogStoreMultimodalConfiguration with ossBucket/roleArn
 *    (data is written to the user's own bucket by assuming roleArn)
 * 2. getLogStoreMultimodalConfiguration to verify the configuration
 * 3. putLogStoreMultimodalConfiguration without ossBucket/roleArn
 *    (data is written to the built-in bucket)
 * 4. getLogStoreMultimodalConfiguration to verify the configuration
 * 5. putObject / getObject / listObjects / deleteObject
 */
public class MultimodalObjectSample {
    private Client client;

    MultimodalObjectSample() {
        String endpoint = ""; // replace with your endpoint, e.g. cn-hangzhou.log.aliyuncs.com
        String accessKeyID = System.getenv("ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ACCESS_KEY_SECRET");

        // create a client
        this.client = new Client(endpoint, accessKeyID, accessKeySecret);
    }

    public static void main(String[] args) {
        MultimodalObjectSample sample = new MultimodalObjectSample();
        String project = "your_project_name"; // replace with your project name
        String logStore = "your_logstore"; // replace with your logstore name
        String objectName = "test_object";

        // Fill in your own bucket and roleArn to verify the "write to user's
        // bucket" scenario. Leave them empty to skip and only verify the
        // built-in bucket scenario.
        String ossBucket = ""; // replace with your OSS bucket name
        String roleArn = ""; // replace with your role arn, e.g. acs:ram::<uid>:role/<roleName>

        // Disable multimodal
        runStep("putMultimodalConfiguration(Disabled)", new SampleStep() {
            @Override
            public void run() throws Exception {
                sample.putMultimodalConfiguration(project, logStore, MultimodalStatus.DISABLED, null, null);
                sample.getMultimodalConfiguration(project, logStore);
            }
        });

        // Enable multimodal with the user bucket configuration
        runStep("putMultimodalConfiguration(Enabled)", new SampleStep() {
            @Override
            public void run() throws Exception {
                sample.putMultimodalConfiguration(project, logStore, MultimodalStatus.ENABLED, ossBucket, roleArn);
                sample.getMultimodalConfiguration(project, logStore);
            }
        });

        // Object operations
        runStep("putObject", new SampleStep() {
            @Override
            public void run() throws Exception {
                sample.putObject(project, logStore, objectName);
            }
        });
        runStep("getObject", new SampleStep() {
            @Override
            public void run() throws Exception {
                sample.getObject(project, logStore, objectName);
            }
        });
        runStep("listObjects", new SampleStep() {
            @Override
            public void run() throws Exception {
                sample.listObjects(project, logStore);
            }
        });
        runStep("presignedUrl(PUT&GET)", new SampleStep() {
            @Override
            public void run() throws Exception {
                // Presigned urls expire after 600 seconds
                long expiresInSeconds = 600;

                // Generate a presigned url for writing (PUT) and use it to upload
                String putUrl = sample.generatePresignedUrl(project, logStore, objectName, "PUT", expiresInSeconds);
                sample.uploadWithPresignedUrl(putUrl, "Hello, uploaded via presigned PUT url");

                // Generate a presigned url for reading (GET) and use it to download
                String getUrl = sample.generatePresignedUrl(project, logStore, objectName, "GET", expiresInSeconds);
                sample.downloadWithPresignedUrl(getUrl);
            }
        });
        runStep("deleteObject", new SampleStep() {
            @Override
            public void run() throws Exception {
                sample.deleteObject(project, logStore, objectName);
            }
        });
        runStep("listObjects(afterDelete)", new SampleStep() {
            @Override
            public void run() throws Exception {
                sample.listObjects(project, logStore);
            }
        });
    }

    /**
     * A sample step that may throw checked exceptions.
     */
    private interface SampleStep {
        void run() throws Exception;
    }

    /**
     * Run the step and print the error instead of aborting the whole flow on
     * failure, so that every step's result can be observed.
     */
    private static void runStep(String name, SampleStep step) {
        try {
            step.run();
        } catch (LogException e) {
            System.out.println("Step [" + name + "] failed!");
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Error requestId: " + e.getRequestId());
        } catch (Exception e) {
            System.out.println("Step [" + name + "] failed: " + e.getMessage());
        }
    }

    /**
     * Put logstore multimodal configuration.
     * If ossBucket and roleArn are provided, SLS assumes roleArn to write the
     * user's own bucket; otherwise data is written to the built-in bucket.
     */
    public void putMultimodalConfiguration(String project, String logStore, MultimodalStatus status,
                                           String ossBucket, String roleArn)
            throws LogException {
        System.out.println("============================================================");
        System.out.println("Sample: Put LogStore Multimodal Configuration");
        System.out.println("============================================================");

        PutLogStoreMultimodalConfigurationRequest request = new PutLogStoreMultimodalConfigurationRequest(
                project, logStore, status);
        if (ossBucket != null && !ossBucket.isEmpty()) {
            request.setOssBucket(ossBucket);
        }
        if (roleArn != null && !roleArn.isEmpty()) {
            request.setRoleArn(roleArn);
        }
        System.out.println("Request body: " + request.getRequestBody());
        VoidResponse response = client.putLogStoreMultimodalConfiguration(request);
        System.out.println("Put multimodal configuration success! ossBucket: "
                + (ossBucket == null ? "(built-in)" : ossBucket) + ", roleArn: "
                + (roleArn == null ? "(built-in)" : roleArn));
        System.out.println("RequestId: " + response.GetRequestId());
    }

    /**
     * Get logstore multimodal configuration and print it.
     */
    public void getMultimodalConfiguration(String project, String logStore) throws LogException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Get LogStore Multimodal Configuration");
        System.out.println("============================================================");

        GetLogStoreMultimodalConfigurationResponse response = client.getLogStoreMultimodalConfiguration(
                new GetLogStoreMultimodalConfigurationRequest(project, logStore));
        System.out.println("Get multimodal configuration success!");
        System.out.println("Status: " + response.getStatus());
        System.out.println("AnonymousWrite: " + response.getAnonymousWrite());
        System.out.println("OssBucket: " + response.getOssBucket());
        System.out.println("RoleArn: " + response.getRoleArn());
        System.out.println("RequestId: " + response.GetRequestId());
    }

    /**
     * Put a simple text object.
     */
    public void putObject(String project, String logStore, String objectName) throws LogException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Put Object");
        System.out.println("============================================================");

        String content = "Hello, this is multimodal test content";
        InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        PutObjectResponse response = client.putObject(project, logStore, objectName, contentStream);
        System.out.println("Put object success!");
        System.out.println("ETag: " + response.getETag());
        System.out.println("RequestId: " + response.GetRequestId());
    }

    /**
     * Get an object using try-with-resources.
     */
    public void getObject(String project, String logStore, String objectName) throws LogException, IOException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Get Object");
        System.out.println("============================================================");

        try (GetObjectResponse response = client.getObject(project, logStore, objectName)) {
            System.out.println("Get object success!");
            ObjectMetadata metadata = response.getObjectMetadata();
            System.out.println("ETag: " + metadata.getETag());
            System.out.println("Content Type: " + metadata.getContentType());
            System.out.println("Content Length: " + metadata.getContentLength());
            System.out.println("RequestId: " + response.GetRequestId());

            InputStream content = response.getContent();
            if (content != null) {
                byte[] buffer = new byte[1024];
                StringBuilder contentBuilder = new StringBuilder();
                int bytesRead;
                while ((bytesRead = content.read(buffer)) != -1) {
                    contentBuilder.append(new String(buffer, 0, bytesRead, "UTF-8"));
                }
                System.out.println("Content: " + contentBuilder.toString());
            }
        }
        // Response is automatically closed here
    }

    /**
     * List objects in the logstore.
     */
    public void listObjects(String project, String logStore) throws LogException {
        System.out.println("\n============================================================");
        System.out.println("Sample: List Objects");
        System.out.println("============================================================");

        ListObjectsRequest request = new ListObjectsRequest(project, logStore);
        request.setMaxResults(100);
        ListObjectsResponse response = client.listObjects(request);
        System.out.println("List objects success! isTruncated: " + response.getIsTruncated()
                + ", nextToken: " + response.getNextToken()
                + ", count: " + response.getObjects().size());
        System.out.println("RequestId: " + response.GetRequestId());
        for (ObjectSummary summary : response.getObjects()) {
            System.out.println("  key: " + summary.getKey() + ", size: " + summary.getSize()
                    + ", lastModified: " + summary.getLastModified() + ", ETag: " + summary.getETag());
        }
    }

    /**
     * Generate a presigned url for the object.
     *
     * @param httpMethod       the method the presigned url is used for: GET (read) or PUT (write)
     * @param expiresInSeconds the expiration time of the presigned url in seconds
     * @return the presigned url
     */
    public String generatePresignedUrl(String project, String logStore, String objectName, String httpMethod,
                                       long expiresInSeconds)
            throws LogException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Generate Presigned Url (" + httpMethod + ", expires=" + expiresInSeconds + "s)");
        System.out.println("============================================================");

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(project, logStore, objectName,
                httpMethod, expiresInSeconds);
        System.out.println("Request body: " + request.getRequestBody());
        GeneratePresignedUrlResponse response = client.generatePresignedUrl(request);
        System.out.println("Generate presigned url success!");
        System.out.println("Url: " + response.getUrl());
        System.out.println("RequestId: " + response.GetRequestId());
        return response.getUrl();
    }

    /**
     * Upload content to the object via a presigned PUT url (no credentials needed).
     */
    public void uploadWithPresignedUrl(String presignedUrl, String content) throws IOException {
        System.out.println("\n--- Upload via presigned PUT url ---");
        byte[] body = content.getBytes("UTF-8");
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(presignedUrl).openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            OutputStream out = conn.getOutputStream();
            try {
                out.write(body);
            } finally {
                out.close();
            }
            int statusCode = conn.getResponseCode();
            System.out.println("Upload status: " + statusCode);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Download the object content via a presigned GET url (no credentials needed).
     */
    public void downloadWithPresignedUrl(String presignedUrl) throws IOException {
        System.out.println("\n--- Download via presigned GET url ---");
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(presignedUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int statusCode = conn.getResponseCode();
            System.out.println("Download status: " + statusCode);
            InputStream in = statusCode < 400 ? conn.getInputStream() : conn.getErrorStream();
            if (in != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] chunk = new byte[4096];
                int n;
                try {
                    while ((n = in.read(chunk)) != -1) {
                        buffer.write(chunk, 0, n);
                    }
                } finally {
                    in.close();
                }
                System.out.println("Content: " + new String(buffer.toByteArray(), "UTF-8"));
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Delete an object.
     */
    public void deleteObject(String project, String logStore, String objectName) throws LogException {
        System.out.println("\n============================================================");
        System.out.println("Sample: Delete Object");
        System.out.println("============================================================");

        VoidResponse response = client.deleteObject(project, logStore, objectName);
        System.out.println("Delete object success!");
        System.out.println("RequestId: " + response.GetRequestId());
    }
}
