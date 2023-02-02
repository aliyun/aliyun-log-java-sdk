package com.aliyun.openservices.log.response;

import java.util.Map;

public class SetProjectCnameResponse extends Response {
    private String certId;

    public SetProjectCnameResponse(Map<String, String> headers) {
        super(headers);
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }
}
