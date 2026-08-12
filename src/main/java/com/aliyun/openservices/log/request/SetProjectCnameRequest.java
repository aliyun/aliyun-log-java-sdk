package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.internal.json.JSONObject;
import com.aliyun.openservices.log.common.CertificateConfiguration;
import com.aliyun.openservices.log.annotation.InternalApi;

public class SetProjectCnameRequest extends Request {
    private String domain;
    private CertificateConfiguration certificateConfiguration;

    public SetProjectCnameRequest(String project, String domain) {
        super(project);
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public CertificateConfiguration getCertificateConfiguration() {
        return certificateConfiguration;
    }

    public void setCertificateConfiguration(CertificateConfiguration certificateConfiguration) {
        this.certificateConfiguration = certificateConfiguration;
    }

    @InternalApi
    public JSONObject marshal() {
        JSONObject object = new JSONObject();
        object.put("domain", domain);
        if (certificateConfiguration != null) {
            object.put("certificateConfiguration", certificateConfiguration.marshal());
        }
        return object;
    }
}
