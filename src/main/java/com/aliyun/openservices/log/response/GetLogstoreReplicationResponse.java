package com.aliyun.openservices.log.response;


import java.util.Map;

public class GetLogstoreReplicationResponse extends Response {

    private static final long serialVersionUID = 3296060004608027853L;
    private boolean enable;

    public GetLogstoreReplicationResponse(Map<String, String> headers, boolean enable) {
        super(headers);
        this.enable = enable;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
