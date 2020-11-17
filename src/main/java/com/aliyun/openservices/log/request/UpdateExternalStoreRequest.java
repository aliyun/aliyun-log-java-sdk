package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.ExternalStore;

public class UpdateExternalStoreRequest extends Request {

    private static final long serialVersionUID = 8606882056871782816L;

    private ExternalStore externalStore;

    public UpdateExternalStoreRequest(String project, ExternalStore externalStore) {
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
