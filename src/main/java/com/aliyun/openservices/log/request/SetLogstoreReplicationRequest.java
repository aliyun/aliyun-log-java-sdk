package com.aliyun.openservices.log.request;

public class SetLogstoreReplicationRequest extends Request {

    private static final long serialVersionUID = 7579138643389312274L;
    private String logStore;
    private boolean enable;

    public SetLogstoreReplicationRequest(String project, String logStore, boolean enable) {
        super(project);
        setLogStore(logStore);
        setEnable(enable);
    }

    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    public String getLogStore() {
        return this.logStore;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getEnable() {
        return enable;
    }

}
