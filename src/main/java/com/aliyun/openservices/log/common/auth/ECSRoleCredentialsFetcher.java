package com.aliyun.openservices.log.common.auth;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.utils.DateUtil;
import com.aliyun.openservices.log.internal.ErrorCodes;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.text.ParseException;


public class ECSRoleCredentialsFetcher extends HttpCredentialsFetcher {
    private static final String META_DATA_SERVICE_URL = "http://100.100.100.200/latest/meta-data/ram/security-credentials/";

    public ECSRoleCredentialsFetcher(String ecsRamRole) {
        this.ecsRamRole = ecsRamRole;
    }

    @Override
    public String buildUrl() {
        return META_DATA_SERVICE_URL + ecsRamRole;
    }

    private TemporaryCredentials credentialsFromJson(JSONObject response) throws ParseException {
        String accessKeyId = response.getString("AccessKeyId");
        String accessKeySecret = response.getString("AccessKeySecret");
        String securityToken = response.getString("SecurityToken");
        long expiration = DateUtil.stringToLong(response.getString("Expiration"));
        long lastUpdated = DateUtil.stringToLong(response.getString("LastUpdated"));
        return new TemporaryCredentials(accessKeyId, accessKeySecret,
                securityToken, expiration, lastUpdated);
    }

    @Override
    public TemporaryCredentials parse(HttpResponse httpResponse) throws LogException {
        // parse http response body
        String rawRespBody;
        try {
            rawRespBody = EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            throw new LogException(ErrorCodes.FETCH_CREDENTIALS_FAILED, "Fail to fetch credentials: fetch http resp body", e, "");
        }
        // response body to json object, json object to credentials
        try {
            JSONObject response = JSONObject.parseObject(rawRespBody);
            if (response == null || !"Success".equalsIgnoreCase(response.getString("Code"))) {
                throw new LogException(ErrorCodes.FETCH_CREDENTIALS_FAILED, "Fetch credential got unexpected response, " + rawRespBody, "");
            }
            return credentialsFromJson(response);
        } catch (ParseException e) {
            throw new LogException(ErrorCodes.FETCH_CREDENTIALS_FAILED, "Fetch credential fail to parse response, " + rawRespBody, e, "");
        }
    }

    private final String ecsRamRole;
}
