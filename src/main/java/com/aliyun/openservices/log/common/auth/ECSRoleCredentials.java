package com.aliyun.openservices.log.common.auth;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.http.utils.DateUtil;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ECSRoleCredentials implements Credentials {
    private static final String META_DATA_SERVICE_URL = "http://100.100.100.200/latest/meta-data/ram/security-credentials/";
    private String ecsRamRole;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private long expiration = 0;
    private long lastUpdated = 0;

    private final static double REFRESH_SCALE = 0.8;

    public ECSRoleCredentials(String ecsRamRole) {
        this.ecsRamRole = ecsRamRole;
    }

    private void refreshIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastUpdated > (expiration - lastUpdated) * REFRESH_SCALE) {
            fetchCredentials();
        }
    }

    @Override
    public String getAccessKeyId() {
        refreshIfNeeded();
        return accessKeyId;
    }

    @Override
    public String getAccessKeySecret() {
        refreshIfNeeded();
        return accessKeySecret;
    }

    @Override
    public String getSecurityToken() {
        refreshIfNeeded();
        return securityToken;
    }

    @Override
    public void setAccessKeyId(String accessKeyId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAccessKeySecret(String accessKeySecret) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSecurityToken(String securityToken) {
        throw new UnsupportedOperationException();
    }

    private void fetchCredentials() {
        String requestUrl = META_DATA_SERVICE_URL + ecsRamRole;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(requestUrl);
        CloseableHttpResponse httpResponse = null;
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(3000, TimeUnit.MILLISECONDS)
                    .setConnectionRequestTimeout(3000, TimeUnit.MILLISECONDS)
                    .setResponseTimeout(3000, TimeUnit.MILLISECONDS)
                    .build();
            httpGet.setConfig(config);
            httpResponse = httpClient.execute(httpGet);
            JSONObject response = JSONObject.parseObject(EntityUtils.toString(httpResponse.getEntity()));
            if (response != null && "Success".equalsIgnoreCase(response.getString("Code"))) {
                accessKeyId = response.getString("AccessKeyId");
                accessKeySecret = response.getString("AccessKeySecret");
                securityToken = response.getString("SecurityToken");
                expiration = DateUtil.stringToLong(response.getString("Expiration"));
                lastUpdated = DateUtil.stringToLong(response.getString("LastUpdated"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot fetch credentials with role " + ecsRamRole, e);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
