package com.aliyun.openservices.log.request;

public class GetLogStoreMeteringModeRequest extends Request {

    private String logStore;


    public GetLogStoreMeteringModeRequest(String project, String logStore) {
        super(project);
        this.logStore = logStore;
    }

    public String getLogStore() {
        return logStore;
    }

    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }
}
