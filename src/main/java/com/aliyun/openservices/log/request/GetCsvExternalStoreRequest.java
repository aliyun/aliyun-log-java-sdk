package com.aliyun.openservices.log.request;

public class GetCsvExternalStoreRequest extends Request {

    private static final long serialVersionUID = -5142441401722311361L;

    private String externalStoreName;

    public GetCsvExternalStoreRequest(String project, String externalStoreName) {
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
