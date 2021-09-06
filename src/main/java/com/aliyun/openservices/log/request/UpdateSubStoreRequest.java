package com.aliyun.openservices.log.request;

import com.aliyun.openservices.log.common.SubStore;

public class UpdateSubStoreRequest extends Request {
    private String logStoreName;
    private SubStore subStore;

    public UpdateSubStoreRequest(String project, String logStoreName, SubStore subStore) {
        super(project);
        setLogStoreName(logStoreName);
        setSubStore(subStore);
    }

    public String getLogStoreName() {
        return logStoreName;
    }

    public void setLogStoreName(String logStoreName) {
        this.logStoreName = logStoreName;
    }

    public SubStore getSubStore() {
        return subStore;
    }

    public void setSubStore(SubStore subStore) {
        this.subStore = subStore;
    }
}
