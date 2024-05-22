package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.signer.SignVersion;
import com.aliyun.openservices.log.util.Utils;

public class SignV4Sample {
    public static void main(String[] args) throws LogException {
        String accessKeyId = ""; // replace with your accessKeyId
        String accessKeySecret = ""; // replace with your accessKeySecret
        String endpoint = "cn-hangzhou-intranet.log.aliyuncs.com"; // replace with your endpoint

        ClientConfiguration config = new ClientConfiguration();
        config.setRegion(Utils.parseRegion(endpoint)); // region must be set if using signature v4
        config.setSignatureVersion(SignVersion.V4);

        Client client = new Client(endpoint, new StaticCredentialsProvider(
                new DefaultCredentials(accessKeyId, accessKeySecret)
        ));
        client.GetProject("example-project");
    }
}
