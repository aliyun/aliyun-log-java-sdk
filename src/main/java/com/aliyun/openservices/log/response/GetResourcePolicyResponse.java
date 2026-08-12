package com.aliyun.openservices.log.response;

import com.aliyun.openservices.log.common.ResourcePolicyResourceType;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.comm.ResponseMessage;
import com.aliyun.openservices.log.internal.ErrorCodes;
import com.aliyun.openservices.log.internal.json.JSONException;
import com.aliyun.openservices.log.internal.json.JSONObject;

import java.util.Map;

/**
 * Response returned by the Resource Policy get API.
 */
public class GetResourcePolicyResponse extends Response {
    private final ResourcePolicyResourceType resourceType;
    private final String resourceName;
    private final String policyDocument;
    private final long revision;
    private final long createTime;
    private final long updateTime;

    public GetResourcePolicyResponse(Map<String, String> headers,
                                     ResourcePolicyResourceType resourceType,
                                     String resourceName,
                                     String policyDocument,
                                     long revision,
                                     long createTime,
                                     long updateTime) {
        super(headers);
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.policyDocument = policyDocument;
        this.revision = revision;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public static GetResourcePolicyResponse fromResponse(ResponseMessage response) throws LogException {
        String body = response.GetStringBody();
        try {
            JSONObject object = JSONObject.parseObject(body);
            String resourceName = object.getString("resourceName");
            return new GetResourcePolicyResponse(response.getHeaders(),
                    ResourcePolicyResourceType.fromValue(object.getString("resourceType")),
                    resourceName == null ? "" : resourceName,
                    object.getString("policyDocument"),
                    object.getLongValue("revision"),
                    object.getLongValue("createTime"),
                    object.getLongValue("updateTime"));
        } catch (JSONException e) {
            throw new LogException(ErrorCodes.BAD_RESPONSE,
                    "The response is not valid json string : " + body, e, response.getRequestId());
        }
    }

    public ResourcePolicyResourceType getResourceType() {
        return resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getPolicyDocument() {
        return policyDocument;
    }

    public long getRevision() {
        return revision;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }
}
