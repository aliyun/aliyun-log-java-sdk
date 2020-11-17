package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ExternalStore;

public class CreateExternalStoreRequest extends Request {

    private static final long serialVersionUID = 2150612553755558404L;

    private ExternalStore externalStore;
    public CreateExternalStoreRequest(String project, ExternalStore externalStore) {
        super(project);
        this.externalStore = externalStore;
    }

    public ExternalStore getExternalStore() {
        return externalStore;
    }

    public void setExternalStore(ExternalStore externalStore) {
        this.externalStore = externalStore;
    }
}
