package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ResourcePolicyResourceType;
import com.aliyun.openservices.log.internal.json.JSONObject;

/**
 * The request used to put a resource policy.
 */
public class PutResourcePolicyRequest extends Request {
    private static final long serialVersionUID = 1L;

    private ResourcePolicyResourceType resourceType;
    private String resourceName;
    private String policyDocument;
    private boolean dryRun;

    public PutResourcePolicyRequest(String project, ResourcePolicyResourceType resourceType,
                                    String resourceName, String policyDocument) {
        super(project);
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.policyDocument = policyDocument;
    }

    public ResourcePolicyResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourcePolicyResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getPolicyDocument() {
        return policyDocument;
    }

    public void setPolicyDocument(String policyDocument) {
        this.policyDocument = policyDocument;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public String getRequestBody() {
        JSONObject body = new JSONObject();
        body.put("resourceType", resourceType.getValue());
        if (resourceName != null && !resourceName.isEmpty()) {
            body.put("resourceName", resourceName);
        }
        body.put("policyDocument", policyDocument);
        body.put("dryRun", dryRun);
        return body.toString();
    }
}
