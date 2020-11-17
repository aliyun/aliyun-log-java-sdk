package com.aliyun.openservices.log.request;

public class GetExternalStoreRequest extends Request {

    private static final long serialVersionUID = 6856292454298435361L;

    private String externalStoreName;

    public GetExternalStoreRequest(String project, String externalStoreName) {
        super(project);
        this.externalStoreName = externalStoreName;
    }

    public String getExternalStoreName() {
        return externalStoreName;
    }

    public void setExternalStoreName(String externalStoreName) {
        this.externalStoreName = externalStoreName;
    }
}
