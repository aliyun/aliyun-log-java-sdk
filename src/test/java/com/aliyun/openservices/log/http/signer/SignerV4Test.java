package com.aliyun.openservices.log.http.signer;

import com.aliyun.openservices.log.common.auth.DefaultCredentails;
import com.aliyun.openservices.log.common.auth.StaticCredentialsProvider;
import com.aliyun.openservices.log.http.client.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.aliyun.openservices.log.http.signer.SlsV4Signer.CHARSET_UTF_8;

public class SignerV4Test {


    @Test
    public void testSignRequest() {
        SlsV4Signer signer = new SlsV4Signer(new StaticCredentialsProvider(
                new DefaultCredentails("acsddda21dsd", "zxasdasdasw2")),
                "cn-hangzhou");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("hello", "world");
        headers.put("hello-Text", "a12X- ");
        headers.put(" Ko ", "");
        headers.put("x-log-test", "het123");
        headers.put("x-acs-ppp", "dds");
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put(" abc", "efg");
        urlParams.put(" agc ", "");
        urlParams.put("", "efg");
        urlParams.put("A-bc", "eFg");

        byte[] body = "adasd= -asd zcas".getBytes(CHARSET_UTF_8);

        Assert.assertEquals(signer.signRequest(headers, HttpMethod.POST,
                        "/logstores", urlParams, body, "20220808T032330Z"),
                "SLS4-HMAC-SHA256 Credential=acsddda21dsd/20220808/cn-hangzhou/sls/aliyun_v4_request,Signature=" +
                        "a98f5632e93836e63839cd836a54055f480020a9364ca944e2d34f2eb9bf1bed");

        SlsV4Signer signer2 = new SlsV4Signer(new StaticCredentialsProvider(
                new DefaultCredentails("acsddda21dsd", "zxasdasdasw2")),
                "cn-shanghai");

        Assert.assertEquals(signer2.signRequest(new HashMap<String, String>(),
                        HttpMethod.POST, "/logstores", new HashMap<String, String>(), body,
                        "20220808T032330Z"),
                "SLS4-HMAC-SHA256 Credential=acsddda21dsd/20220808/cn-shanghai/sls/aliyun_v4_request,Signature=" +
                        "8a10a5e723cb2e75964816de660b2c16a58af8bc0261f7f0722d832468c76ce8");

        Assert.assertEquals(signer.signRequest(headers, HttpMethod.POST,
                        "/logstores", urlParams, "".getBytes(CHARSET_UTF_8),
                        "20220808T032330Z"),
                "SLS4-HMAC-SHA256 Credential=acsddda21dsd/20220808/cn-hangzhou/sls/aliyun_v4_request,Signature=" +
                        "5a66d8f8051983e0e9d08e0f960ef9252ef971eead5bb5c7acec8617a2eb2701");

        Assert.assertEquals(signer.signRequest(headers, HttpMethod.GET,
                        "/logstores", urlParams, "".getBytes(CHARSET_UTF_8),
                        "20220808T032330Z"),
                "SLS4-HMAC-SHA256 Credential=acsddda21dsd/20220808/cn-hangzhou/sls/aliyun_v4_request,Signature=" +
                        "d92741852500791d662a8d469ff61627c0559ecd86c3f59b7bf6772b6c62666a");

        urlParams.put("abs-ij*asd/vc", "a~js+d ada");
        urlParams.put("a abAas123/vc", "a~jdad a2ADFs+d ada");
        Assert.assertEquals(signer.signRequest(headers, HttpMethod.POST,
                        "/logstores/hello/a+*~bb/cc", urlParams, body,
                        "20220808T032330Z"),
                "SLS4-HMAC-SHA256 Credential=acsddda21dsd/20220808/cn-hangzhou/sls/aliyun_v4_request,Signature=" +
                        "2c204068e961a8813a6bcf7ac422f7fa6e9bf9a5da493e0165dfe100854d18ff");
    }
}
