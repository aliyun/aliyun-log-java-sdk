package com.aliyun.openservices.log.request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.common.CertificateConfiguration;

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

    public JSONObject marshal() {
        JSONObject object = new JSONObject();
        object.put("domain", domain);
        if (certificateConfiguration != null) {
            object.put("certificateConfiguration", certificateConfiguration.marshal());
        }
        return object;
    }
}
