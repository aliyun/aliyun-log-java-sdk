package com.aliyun.openservices.log.request;

public class GetLogstoreReplicationRequest extends Request {

    private static final long serialVersionUID = 7579138643389312274L;
    private String logStore;
    protected boolean enable;

    public GetLogstoreReplicationRequest(String project, String logStore) {
        super(project);
        setLogStore(logStore);
    }

    public void setLogStore(String logStore) {
        this.logStore = logStore;
    }

    public String getLogStore() {
        return logStore;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getEnable() {
        return enable;
    }
}
