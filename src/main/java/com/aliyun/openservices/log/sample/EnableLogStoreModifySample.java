package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.EnableLogStoreModifyRequest;
import com.aliyun.openservices.log.response.VoidResponse;

/**
 * Sample code for enabling Store Modify on an existing logstore.
 *
 * <p>This operation is one-way. Use a test logstore first and make sure that
 * enabling Store Modify is appropriate before running the sample.</p>
 */
public class EnableLogStoreModifySample {

    private static final String ENDPOINT = "your-sls-endpoint";
    private static final String ACCESS_KEY_ID = "your-access-key-id";
    private static final String ACCESS_KEY_SECRET = "your-access-key-secret";
    private static final String PROJECT = "your-project";
    private static final String LOGSTORE = "your-logstore";

    public static void main(String[] args) {
        Client client = new Client(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        EnableLogStoreModifyRequest request = new EnableLogStoreModifyRequest(PROJECT, LOGSTORE);

        try {
            VoidResponse response = client.enableLogStoreModify(request);
            System.out.println("Enable Store Modify request accepted.");
            System.out.println("Request ID: " + response.GetRequestId());
            System.out.println("The conversion runs asynchronously; acceptance does not mean it has completed.");
        } catch (LogException e) {
            System.err.println("Failed to enable Store Modify.");
            System.err.println("Error code: " + e.GetErrorCode());
            System.err.println("Error message: " + e.GetErrorMessage());
            System.err.println("Request ID: " + e.GetRequestId());
        }
    }
}
