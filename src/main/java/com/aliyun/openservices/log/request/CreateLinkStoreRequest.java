package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.LinkStore;

public class CreateLinkStoreRequest extends Request {
    private static final long serialVersionUID = -2657062591733503370L;
    private LinkStore linkStore;

    public CreateLinkStoreRequest(String project, LinkStore linkStore) {
        super(project);
        setLinkStore(linkStore);
    }

    public LinkStore getLinkStore() {
        return linkStore;
    }

    public void setLinkStore(LinkStore linkStore) {
        this.linkStore = linkStore;
    }
}
