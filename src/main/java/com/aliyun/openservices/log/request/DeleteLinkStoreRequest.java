package com.aliyun.openservices.log.request;

public class DeleteLinkStoreRequest extends Request {
    private static final long serialVersionUID = -5478188598510366229L;

    protected String linkStoreName;

    public DeleteLinkStoreRequest(String project, String linkStoreName) {
        super(project);
        this.linkStoreName = linkStoreName;

        super.SetParam("type", "link");
    }

    public String getLinkStoreName() {
        return linkStoreName;
    }

    public void setLinkStoreName(String linkStoreName) {
        this.linkStoreName = linkStoreName;
    }
}