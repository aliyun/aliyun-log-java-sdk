package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetMetricStoreMeteringModeRequest;
import com.aliyun.openservices.log.request.UpdateMetricStoreMeteringModeRequest;
import com.aliyun.openservices.log.response.GetMetricStoreMeteringModeResponse;
import com.aliyun.openservices.log.response.VoidResponse;

/**
 * MetricStore Metering Mode Sample
 */
public class MetricStoreMeteringModeSample {

    public static void main(String[] args) {
        // ======== Please fill in your configuration ========
        String endpoint = "cn-hangzhou.log.aliyuncs.com";
        String accessKeyId = "";         // Replace with your AccessKeyId
        String accessKeySecret = "";     // Replace with your AccessKeySecret
        String project = "";             // Replace with your project name
        String metricStore = "";         // Replace with your MetricStore name
        // ===================================================

        // Create client
        Client client = new Client(endpoint, accessKeyId, accessKeySecret);

        try {
            System.out.println("==========================================");
            System.out.println("MetricStore Metering Mode Test");
            System.out.println("Project: " + project);
            System.out.println("MetricStore: " + metricStore);
            System.out.println("==========================================\n");

            // 1. Get current metering mode
            System.out.println("1. Getting current metering mode...");
            GetMetricStoreMeteringModeRequest getRequest = new GetMetricStoreMeteringModeRequest(project, metricStore);
            GetMetricStoreMeteringModeResponse getResponse = client.getMetricStoreMeteringMode(getRequest);

            System.out.println("   Current metering mode: " + getResponse.getMeteringMode());
            System.out.println("   Request ID: " + getResponse.GetRequestId());
            System.out.println();

            // 2. Update metering mode (optional, enabled for testing)

            System.out.println("2. Updating metering mode...");
            String newMeteringMode = "ChargeByDataIngest"; // or "ChargeByFunction"
            UpdateMetricStoreMeteringModeRequest updateRequest = new UpdateMetricStoreMeteringModeRequest(project,
                    metricStore, newMeteringMode);
            VoidResponse updateResponse = client.updateMetricStoreMeteringMode(updateRequest);

            System.out.println("   Update successful!");
            System.out.println("   New metering mode: " + newMeteringMode);
            System.out.println("   Request ID: " + updateResponse.GetRequestId());
            System.out.println();

            // 3. Verify the update result
            System.out.println("3. Verifying update result...");
            GetMetricStoreMeteringModeResponse verifyResponse = client.getMetricStoreMeteringMode(getRequest);
            System.out.println("   Verified metering mode: " + verifyResponse.getMeteringMode());
            System.out.println("   Request ID: " + verifyResponse.GetRequestId());

        } catch (LogException e) {
            System.err.println("Error occurred:");
            System.err.println("  Error code: " + e.GetErrorCode());
            System.err.println("  Error message: " + e.GetErrorMessage());
            System.err.println("  Request ID: " + e.GetRequestId());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unknown error occurred:");
            e.printStackTrace();
        }
    }
}
