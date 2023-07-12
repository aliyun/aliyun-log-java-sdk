package com.aliyun.openservices.log.common.auth;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.utils.DateUtil;
import com.aliyun.openservices.log.internal.ErrorCodes;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;


public class ECSRoleCredentialsFetcher extends HttpCredentialsFetcher {
    private static final String META_DATA_SERVICE_URL = "http://100.100.100.200/latest/meta-data/ram/security-credentials/";

    public ECSRoleCredentialsFetcher(String ecsRamRole) {
        this.ecsRamRole = ecsRamRole;
    }

    @Override
    public String buildUrl() {
        return META_DATA_SERVICE_URL + ecsRamRole;
    }

    @Override
    public TemporaryCredentials parse(HttpResponse httpResponse) throws LogException {
        try {
            JSONObject response = JSONObject.parseObject(EntityUtils.toString(httpResponse.getEntity()));
            if (response != null && "Success".equalsIgnoreCase(response.getString("Code"))) {
                String accessKeyId = response.getString("AccessKeyId");
                String accessKeySecret = response.getString("AccessKeySecret");
                String securityToken = response.getString("SecurityToken");
                long expiration = DateUtil.stringToLong(response.getString("Expiration"));
                long lastUpdated = DateUtil.stringToLong(response.getString("LastUpdated"));
                return new TemporaryCredentials(accessKeyId, accessKeySecret,
                        securityToken, expiration, lastUpdated);
            }
            throw new LogException(ErrorCodes.CREDENTIAL_BAD_RESPONSE, "Fetch credential got bad response", "");
        } catch (Exception e) {
            throw new LogException(ErrorCodes.CREDENTIAL_BAD_RESPONSE,
                    "Fetch credential got bad response, err msg:" + e.getMessage(), "");
        }
    }

    private final String ecsRamRole;
}
