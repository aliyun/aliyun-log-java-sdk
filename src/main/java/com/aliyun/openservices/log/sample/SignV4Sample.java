package com.aliyun.openservices.log.sample;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.signer.SignVersion;
import com.aliyun.openservices.log.util.Utils;

public class SignV4Sample {
    public static void main(String[] args) throws LogException {
        String accessKeyId = ""; // replace with your accessKeyId
        String accessKeySecret = ""; // replace with your accessKeySecret
        String endpoint = "cn-hangzhou-intranet.log.aliyuncs.com"; // replace with your endpoint
        Client client = new Client(endpoint, new StaticCredentialsProvider(
                new DefaultCredentials(accessKeyId, accessKeySecret)
        ));
        client.setSignatureVersion(SignVersion.V4);
        client.setRegion(Utils.parseRegion(endpoint));

        client.GetProject("example-project");
    }
}
