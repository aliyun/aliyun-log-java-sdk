package com.aliyun.openservices.log.request;

public class DeleteExternalStoreRequest extends Request {
    private static final long serialVersionUID = 6705543609927836668L;

    private String externalStoreName;

    public DeleteExternalStoreRequest(String project, String externalStoreName) {
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
